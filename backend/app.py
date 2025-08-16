from flask import Flask, request, jsonify
from flask_cors import CORS
from weather_module import get_weather
from seasons_module import get_season_info
from gallery_module import get_images
from chatbot_module import get_response
from db_module import init_db, log_search, get_search_statistics
import logging
import traceback
from datetime import datetime

# Import additional modules for GenAI and config loading
import json
from openai import OpenAI

# Load API keys from your config file
with open("../config/api_keys.json") as f:
    keys = json.load(f)

openai_client = OpenAI(api_key=keys["openai"])

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Logging config
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.FileHandler('tripmate.log'), logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

# Initialize DB at startup
init_db()

# GPT-based City Intro
def get_city_intro(city):
    prompt = f"Give me a very short (2 sentence) tourist introduction to the city {city}."
    response = openai_client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content.strip()

# ---- MAIN SEARCH ENDPOINT ----
@app.route('/search_place', methods=['GET'])
def search_place():
    city = request.args.get('city', '').strip()
    if not city:
        return jsonify({"error": "City parameter is required"}), 400

    logger.info(f"Processing search for {city}")

    # app.py (inside search_place)
    try:
        weather = get_weather(city)
        temp_min = weather["temp_min"]
        temp_max = weather["temp_max"]
        description = weather["description"]

        season_info = get_season_info(city, weather.get("lat"))  # ok if your func uses only lat

        images = get_images(city)
        intro = get_city_intro(city)
        log_search(city)

        return jsonify({
            "success": True,
            "city": city,
            "intro": intro,
            "weather": {
                "temp_min": temp_min,
                "temp_max": temp_max,
                "description": description
            },
            "seasons": season_info,
            "images": images,
        })

    except Exception as e:
        logger.error("Search error for %s: %s", city, traceback.format_exc())
        return jsonify({"success": False, "error": "Unable to process search"}), 500


# ---- BAR CHART DATA ENDPOINT ----
@app.route('/search_statistics', methods=['GET'])
def search_statistics():
    try:
        data = get_search_statistics()
        stats = [{"city": city, "count": count} for city, count in data]
        return jsonify({"success": True, "data": stats})
    except Exception as e:
        logger.error(traceback.format_exc())
        return jsonify({"success": False, "error": "Failed to get statistics"}), 500

# ---- KEEP OTHER EXISTING ROUTES UNCHANGED ----
# (health, chatbot, place-info, etc.)

@app.route('/chatbot', methods=['GET'])
def chatbot():
    query = request.args.get('query', '')
    if not query:
        return jsonify({"reply": "Please ask something."})
    
    try:
        reply = get_response(query)
        return jsonify({"reply": reply})
    except Exception as e:
        logger.error(traceback.format_exc())
        return jsonify({"reply": "Sorry, something went wrong with the chatbot."})



# MAIN
if __name__ == '__main__':
    app.run(debug=True)
