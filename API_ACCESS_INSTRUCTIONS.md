### 🔑 API Access Instructions

#### 1. **OpenWeather API**

Used for:

* Geocoding (city → latitude & longitude)
* Current weather (One Call API)
* 5-day / 3-hour weather forecast

**Steps to Access:**

1. Go to: [https://openweathermap.org/api](https://openweathermap.org/api)
2. Sign up for a free account.
3. Go to your [API keys page](https://home.openweathermap.org/api_keys)
4. Generate a new API key.

---

#### 2. **Unsplash API**

Used for:

* Fetching high-quality images of cities

**Steps to Access:**

1. Go to: [https://unsplash.com/developers](https://unsplash.com/developers)
2. Sign up and click **"Your Apps"**
3. Register a new application.
4. Copy the **Access Key** shown on the dashboard.

---

#### 3. **OpenAI API**

Used for:

* Generating tourist introductions
* Classifying user inputs (flight/hotel/QA)
* Answering general travel questions

**Steps to Access:**

1. Go to: [https://platform.openai.com/signup](https://platform.openai.com/signup)
2. Sign in to your account.
3. Visit the [API keys page](https://platform.openai.com/account/api-keys)
4. Create a new **secret key**.

---

#### 4. **Amadeus API**

Used for:

* Fetching IATA airport codes
* Flight search (origin, destination, date)
* Hotel search (by city and date)

**Steps to Access:**

1. Go to: [https://developers.amadeus.com/](https://developers.amadeus.com/)
2. Sign up and log in.
3. Create a new **Self-Service Application**.
4. Retrieve your **Client ID** and **Client Secret** from the dashboard.



Sure! Here’s the **final instruction** you can add to the bottom of your `API_ACCESS_INSTRUCTIONS.md` file:

---

### 📁 Where to Place Your API Keys

After generating your API keys as per the instructions above:

✅ **Create a file named** `api_keys.json`
✅ **Place it inside the folder**: `config/`
✅ This file should look like:

```json
{
  "openai": "your_openai_key_here",
  "unsplash": "your_unsplash_key_here",
  "openweather": "your_openweather_key_here",
  "amadeus_client_id": "your_amadeus_client_id",
  "amadeus_client_secret": "your_amadeus_client_secret"
}
```

⚠️ **Important:** : To run TripMate on your system, first download the project. After downloading, you can create the required api_keys.json file.

---

Let me know if you want me to auto-generate the whole `API_ACCESS_INSTRUCTIONS.md` file for your repo with formatting.


