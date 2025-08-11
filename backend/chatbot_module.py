import json
import datetime
import dateparser
import traceback
from amadeus import Client, ResponseError
from openai import OpenAI

# ------------------- Load API Keys -------------------
try:
    with open('../config/api_keys.json', 'r') as f:
        api_config = json.load(f)

    # OpenAI
    openai_key = api_config.get('openai', '')
    client = OpenAI(api_key=openai_key) if openai_key else None
    if not openai_key:
        print("⚠ Warning: OpenAI API key missing.")

    # Amadeus
    amadeus_id = api_config.get('amadeus_client_id', '')
    amadeus_secret = api_config.get('amadeus_client_secret', '')
    amadeus = Client(client_id=amadeus_id, client_secret=amadeus_secret) if (amadeus_id and amadeus_secret) else None
    if not amadeus_id or not amadeus_secret:
        print("⚠ Warning: Amadeus API credentials missing.")

except Exception as e:
    print(f"❌ Failed to load API config: {e}")
    client = None
    amadeus = None

# ------------------- Utilities -------------------
iata_cache = {}

def format_date(date_str):
    try:
        dt = dateparser.parse(date_str)
        return dt.strftime("%Y-%m-%d")
    except:
        return None
def city_to_iata(city_name, prefer_city=True):
    if not city_name:
        return None
    city_key = city_name.strip().title()

    if city_key in iata_cache:
        return iata_cache[city_key]

    if not amadeus:
        print("⚠ Amadeus API not initialized.")
        return None

    try:
        resp = amadeus.reference_data.locations.get(
            keyword=city_key,
            subType="CITY,AIRPORT"
        )
        if resp.data:
            data = resp.data
            # Prefer CITY codes if requested
            if prefer_city:
                city_items = [x for x in data if "CITY" in x.get("subType","")]
                if city_items:
                    code = city_items[0].get("iataCode")
                else:
                    code = data[0].get("iataCode")
            else:
                code = data[0].get("iataCode")

            if code:
                iata_cache[city_key] = code
                return code
    except Exception as e:
        print(f"❌ IATA lookup failed for {city_name}: {e}")
    return None


# ------------------- NLP Extraction -------------------
def classify_and_extract(user_input):
    """Classify input & extract relevant details via GPT, fallback if unavailable."""
    if not client:
        user_lower = user_input.lower()
        if any(word in user_lower for word in ['flight', 'fly', 'plane', 'airline']):
            return {"type": "search", "intent": "flight"}
        elif any(word in user_lower for word in ['hotel', 'stay', 'accommodation', 'room']):
            return {"type": "search", "intent": "hotel"}
        else:
            return {"type": "qa"}

    prompt = f"""
Classify the user input and extract relevant data for travel booking.

Rules:
- from_city and to_city should be ONLY the city name, no country or airport.
- Use the most common spelling, e.g., "New Delhi", "London".
- city is only used for hotel searches.
- date should be exactly as mentioned, even if partial.

Return strictly valid JSON in this format:
{{
  "type": "search" or "qa",
  "intent": "flight" or "hotel" or "",
  "from_city": "",
  "to_city": "",
  "city": "",
  "date": ""
}}

User: "{user_input}"
"""

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[{"role": "user", "content": prompt}],
            temperature=0
        )
        reply = response.choices[0].message.content
        print("🔍 Parsed Data:", reply)
        start, end = reply.find('{'), reply.rfind('}') + 1
        return json.loads(reply[start:end])
    except Exception as e:
        print("❌ Classification failed:", e)
        traceback.print_exc()
        return {"type": "qa"}

# ------------------- Flight & Hotel Search -------------------
# Local mapping of airline codes to names (expand as needed)
AIRLINE_NAMES = {
    "AI": "Air India",
    "UA": "United Airlines",
    "DL": "Delta Air Lines",
    "AA": "American Airlines",
    "BA": "British Airways",
    "QR": "Qatar Airways",
    "EK": "Emirates",
    "SQ": "Singapore Airlines",
    "LH": "Lufthansa",
    "AF": "Air France"
}

def search_flights(origin, destination, departure_date):
    try:
        response = amadeus.shopping.flight_offers_search.get(
            originLocationCode=origin,
            destinationLocationCode=destination,
            departureDate=departure_date,
            adults=1,
            max=3
        )
        offers = response.data
        if not offers:
            return "No flights found."

        output = ["\n--- ✈ Flight Options ---\n"]
        for offer in offers:
            price = float(offer['price']['total'])
            currency = offer['price']['currency']  # Use currency from API

            airline_code = offer['validatingAirlineCodes'][0]
            airline_name = AIRLINE_NAMES.get(airline_code, airline_code)

            # First segment departure
            dep_segment = offer['itineraries'][0]['segments'][0]
            dep_time = datetime.datetime.fromisoformat(dep_segment['departure']['at']).strftime("%d %b %Y, %I:%M %p")
            dep_airport = dep_segment['departure']['iataCode']

            # Last segment arrival
            arr_segment = offer['itineraries'][0]['segments'][-1]
            arr_time = datetime.datetime.fromisoformat(arr_segment['arrival']['at']).strftime("%d %b %Y, %I:%M %p")
            arr_airport = arr_segment['arrival']['iataCode']

            # Duration
            duration = offer['itineraries'][0].get('duration', '').replace("PT", "").lower()

            # Append formatted flight info
            output.append(
                f"Airline       : {airline_name} ({airline_code})\n"
                f"From          : {dep_airport} at {dep_time}\n"
                f"To            : {arr_airport} at {arr_time}\n"
                f"Duration      : {duration}\n"
                f"Price         : {currency} {price:,.2f}\n"
                "------------------------------------"
            )

        return "\n".join(output)

    except ResponseError as e:
        return f"Flight search failed: {str(e)}"

def search_hotels(city_code, check_in, check_out):
    try:
        response = amadeus.shopping.hotel_offers.get(
            cityCode=city_code,
            checkInDate=check_in,
            checkOutDate=check_out,
            adults=1
        )
        offers = response.data
        if not offers:
            return "No hotels found."
        output = []
        for hotel in offers[:3]:
            name = hotel.get('hotel', {}).get('name', 'Unknown Hotel')
            price = hotel.get('offers', [{}])[0].get('price', {}).get('total', 'N/A')
            output.append(f"🏨 {name}: ₹{price} total")
        return "\n".join(output)
    except ResponseError as e:
        return f"Hotel search failed: {str(e)}"

# ------------------- General Travel Q&A -------------------
def answer_travel_question(user_input):
    if not client:
        return "I'm in limited mode — please check an official travel site for more details."
    try:
        prompt = f"You are a helpful travel assistant. Answer this:\n\nUser: {user_input}"
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.5
        )
        return response.choices[0].message.content
    except Exception as e:
        print("❌ GPT Q&A failed:", e)
        traceback.print_exc()
        return "Sorry, I couldn't answer that right now."

# ------------------- Main Handler -------------------
def get_response(user_input):
    try:
        parsed = classify_and_extract(user_input)
        print("🧠 Final Parsed:", parsed)

        intent_type = parsed.get("type", "")
        intent = parsed.get("intent", "")
        date = format_date(parsed.get("date", ""))

        if intent_type == "qa" or intent not in ["flight", "hotel"]:
            return answer_travel_question(user_input)

        if intent == "flight":
            from_city = city_to_iata(parsed.get("from_city", ""), prefer_city=False)
            to_city   = city_to_iata(parsed.get("to_city", ""),   prefer_city=False)

            if not from_city or not to_city:
                return "Please provide valid departure and destination cities."
            if from_city == to_city:
                return "Departure and destination cannot be the same."
            if not date or date <= datetime.date.today().strftime("%Y-%m-%d"):
                return "Please provide a valid future date."

            return search_flights(from_city, to_city, date)
                
        if intent == "hotel":
            city_code = city_to_iata(parsed.get("city", ""))
            if not city_code:
                return "Please specify a valid city for the hotel search."
            if not date:
                return "Please provide a valid check-in date."
            check_out = (datetime.datetime.strptime(date, "%Y-%m-%d") + datetime.timedelta(days=2)).strftime("%Y-%m-%d")
            return search_hotels(city_code, date, check_out)

        return "Try asking me about flights, hotels, or travel tips! 😊"

    except Exception as e:
        print("❌ ERROR in get_response():", str(e))
        traceback.print_exc()
        return "Sorry, I couldn’t answer that right now."
