import requests
import json
# weather_module.py
import requests, json
from statistics import mode

def _load_key():
    with open("../config/api_keys.json") as f:
        return json.load(f)["openweather"]

def _check_ok(resp_json):
    # OpenWeather error payloads often contain 'cod' != 200
    cod = str(resp_json.get("cod", "200"))
    if cod != "200":
        msg = resp_json.get("message", "Unknown error")
        raise Exception(f"OpenWeather API error: {cod} {msg}")

def get_weather(city: str):
    API = _load_key()

    # 1) Geocode -> lat, lon
    g = requests.get(
        "http://api.openweathermap.org/geo/1.0/direct",
        params={"q": city, "limit": 1, "appid": API},
        timeout=10
    ).json()
    if not g:
        raise Exception(f"Could not geocode city '{city}'")
    lat, lon = g[0]["lat"], g[0]["lon"]

    # Helper for final shape
    def _pack(min_c, max_c, desc):
        # if min==max, nudge for nicer display
        if round(min_c,1) == round(max_c,1):
            min_c -= 0.5
            max_c += 0.5
        return {
            "lat": lat, "lon": lon,
            "temp_min": round(float(min_c), 1),
            "temp_max": round(float(max_c), 1),
            "description": desc
        }

    # 2) Try One Call 3.0 (daily)
    one = requests.get(
        "https://api.openweathermap.org/data/3.0/onecall",
        params={"lat": lat, "lon": lon, "exclude": "minutely,hourly,alerts",
                "appid": API, "units": "metric"},
        timeout=10
    ).json()

    # If it’s an error payload, it won’t have 'daily'
    if isinstance(one, dict) and "daily" in one and one["daily"]:
        today = one["daily"][0]
        return _pack(today["temp"]["min"], today["temp"]["max"],
                     today["weather"][0]["description"])

    # 3) Fallback: 5-day / 3-hour forecast -> compute next-24h min/max
    fc = requests.get(
        "https://api.openweathermap.org/data/2.5/forecast",
        params={"lat": lat, "lon": lon, "appid": API, "units": "metric", "cnt": 8},  # ~24h
        timeout=10
    ).json()
    _check_ok(fc)
    temps = [item["main"]["temp"] for item in fc["list"]]
    descs = [item["weather"][0]["description"] for item in fc["list"]]
    return _pack(min(temps), max(temps), mode(descs))

    # (No need for current-weather last resort; this path should succeed.
    # If you still want it, wrap the forecast block in try/except and fall back.)



# def get_weather(city):
#     API_KEY = json.load(open("../config/api_keys.json"))["openweather"]
#     url = f"https://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&units=metric"
#     res = requests.get(url).json()

#     if res.get("cod") != 200:
#         raise Exception(f"OpenWeather API error: {res.get('message', 'Unknown error')}")

#     temp_min = res["main"]["temp_min"]
#     temp_max = res["main"]["temp_max"]

#     # If min == max, adjust by ±0.5°C for display
#     if temp_min == temp_max:
#         temp_min -= 0.5
#         temp_max += 0.5

#     return {
#         "lat": res["coord"]["lat"],
#         "lon": res["coord"]["lon"],
#         "temp_min": round(temp_min, 1),
#         "temp_max": round(temp_max, 1),
#         "description": res["weather"][0]["description"]
#     }
