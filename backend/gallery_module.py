import requests
import json

def get_images(city):
    key = json.load(open("../config/api_keys.json"))["unsplash"]
    url = f"https://api.unsplash.com/search/photos?query={city}&client_id={key}&per_page=5"
    res = requests.get(url).json()
    return [photo["urls"]["regular"] for photo in res["results"]]
