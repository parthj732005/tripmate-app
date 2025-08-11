---

# TripMate 🌍✈️

**TripMate** is a JavaFX + Flask travel planner with an AI chatbot, providing:

* 🌤 **Weather updates**
* 📅 **Best months to visit**
* 🖼 **City images**
* ✈️ **Flight search**
* 📈 **Trending destinations**

---

## 📋 Prerequisites

Before running TripMate, install:

* **Python 3.9+** → [Download](https://www.python.org/downloads/)
* **Java JDK 17+** → [Download](https://adoptium.net/)
* **JavaFX SDK** (same version as your JDK) → [Download](https://openjfx.io/)
* **Git** → [Download](https://git-scm.com/)
* API keys for:

  * OpenWeather (weather data)
  * Flight API (e.g., Amadeus)
  * Image API (Unsplash/Pexels)
    Store them in `config/api_keys.json` (see `api_keys_example.json` in repo for format).

---

## 📂 Project Structure

```
TripMate/
├── backend/         # Python Flask server & APIs
│   ├── app.py
│   ├── chatbot_module.py
│   ├── db_module.py
│   ├── gallery_module.py
│   ├── seasons_module.py
│   ├── weather_module.py
│   └── database/
│
├── frontend/        # JavaFX UI
│   ├── Main.java
│   ├── HomePage.java
│   ├── resources/
│
├── config/          # Configuration files (no keys)
├── lib/             # External libraries
│   └── json-20210307.jar
└── README.md
```

---

## 🚀 Features

* 🔍 **Search** any city for travel info
* 💬 **AI Chatbot** for travel queries & flight search
* 🌦 **Live Weather** data from OpenWeather API
* 🗓 **Best Months to Visit** based on seasonal trends
* 🖼 **Photo Gallery** of destinations
* 🌎 **Trending Tab** to see popular destinations searched

---

## 🛠 Setup Instructions

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/parthj732005/tripmate-app.git
cd tripmate-app
```

### 2️⃣ Backend Setup (Flask + Python)

```bash
cd backend
pip install -r requirements.txt
python app.py
```

💡 Add your API keys in `config/api_keys.json` (keys not included in repo).

### 3️⃣ Frontend Setup (JavaFX) — Windows Example

```bash
cd frontend
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp "../lib/json-20210307.jar" Main.java
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/json-20210307.jar" Main
```

*(Mac/Linux users need to adjust the JavaFX path format.)*

---

## 📌 Usage

1. Start the backend (`python app.py`)
2. Run the JavaFX frontend
3. Search for a city to see weather, best travel months, and images
4. Chat with the AI for travel recommendations and flight info

---

