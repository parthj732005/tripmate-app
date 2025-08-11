Here’s the complete `README.md` you can copy directly into your GitHub repo:

---

# TripMate 🌍✈️

**TripMate** is a JavaFX + Flask travel planner with an AI chatbot, providing:

* 🌤 **Weather updates**
* 📅 **Best months to visit**
* 🖼 **City images**
* ✈️ **Flight search**
* 📈 **Trending destinations**

Built for **Zense Recruitment 2025** and as a personal portfolio project.

---

## 📂 Project Structure

```
TripMate/
├── backend/         # Python Flask server & APIs
│   ├── app.py                # Main Flask app entry
│   ├── chatbot_module.py     # AI chatbot logic
│   ├── db_module.py          # Database handling
│   ├── gallery_module.py     # Image fetching
│   ├── seasons_module.py     # Seasonal/best month info
│   ├── weather_module.py     # Weather API integration
│   └── database/             # DB files
│
├── frontend/        # JavaFX UI
│   ├── Main.java            # Main JavaFX entry
│   ├── HomePage.java        # UI logic
│   ├── resources/           # CSS, icons, and FXML
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

💡 Make sure you add your API keys in `config` (keys not included in repo).

---

### 3️⃣ Frontend Setup (JavaFX)

Navigate to the `frontend` folder and run:

```bash
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp "../lib/json-20210307.jar" Main.java
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/json-20210307.jar" Main
```

---

## 📌 Usage

1. Start the backend (`python app.py`)
2. Run the JavaFX frontend
3. Search for any city and explore its weather, best travel months, and images
4. Chat with the AI for recommendations and flight info

---
