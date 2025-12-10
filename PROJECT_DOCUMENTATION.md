# AQI System - Complete Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Installation & Setup](#installation--setup)
5. [Running the Application](#running-the-application)
6. [Core Features](#core-features)
7. [API Documentation](#api-documentation)
8. [Test Cases & Sample Inputs/Outputs](#test-cases--sample-inputsoutputs)
9. [User Manual](#user-manual)
10. [Troubleshooting](#troubleshooting)
11. [Future Enhancements](#future-enhancements)

---

## Project Overview

### Introduction
The **Intelligent Air Quality Index (AQI) Monitoring System** is a full-stack web application designed to provide real-time air quality monitoring, health alerts, and forecasting capabilities. The system helps users make informed decisions about outdoor activities based on current and predicted air quality conditions.

### Purpose
- Monitor real-time air quality for any city worldwide
- Provide health recommendations based on AQI levels
- Send automated alerts when air quality deteriorates
- Store historical data for trend analysis
- Forecast future air quality conditions

### Key Objectives
- Real-time AQI monitoring using OpenWeatherMap API
- Automated health alerts via email notifications
- Historical data storage and visualization
- Location-based AQI detection using GPS
- 5-day air quality forecasting

---

## System Architecture

### Architecture Overview
The application follows a **3-tier architecture**:

```
┌─────────────────┐
│   React Frontend │  (Port 3000)
│   (User Interface)│
└────────┬────────┘
         │ HTTP/REST API
         │ (CORS Enabled)
┌────────▼────────┐
│ Spring Boot API │  (Port 8080)
│   (Backend)     │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌──▼────────┐
│ MySQL │ │OpenWeather│
│Database│ │   API     │
└───────┘ └───────────┘
```

### Component Structure

#### Backend Components
- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic and API integration
- **Repositories**: Database access layer
- **Models**: Entity classes for database tables
- **Scheduler**: Automated task execution (hourly AQI checks)

#### Frontend Components
- **Home**: Main dashboard with AQI display
- **MapView**: Interactive map visualization
- **Favorites**: Saved city locations
- **Settings**: User preferences and profile
- **SignIn/SignUp**: User authentication

---

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.8
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **API Integration**: OpenWeatherMap API
- **Email Service**: JavaMailSender (Gmail SMTP)

### Frontend
- **Framework**: React 19.2.0
- **Language**: JavaScript (ES6+)
- **Routing**: React Router DOM 7.9.6
- **Charts**: Recharts 3.5.1
- **Maps**: Leaflet 1.9.4, React-Leaflet 5.0.0
- **Icons**: React Icons 5.5.0
- **Build Tool**: Create React App

### External Services
- **OpenWeatherMap API**: Air quality, weather, and forecast data
- **Gmail SMTP**: Email notification service

---

## Installation & Setup

### Prerequisites

#### Required Software
1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **MySQL Server 8.0+**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Verify installation: `mysql --version`

4. **Node.js 16+ and npm**
   - Download from: https://nodejs.org/
   - Verify installation: `node -v` and `npm -v`

5. **Git** (optional, for cloning repository)
   - Download from: https://git-scm.com/

### Backend Setup

#### Step 1: Database Configuration

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE aqi_system;
   ```

2. **Update Database Credentials**
   - Open `src/main/resources/application.properties`
   - Update the following properties:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/aqi_system
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

#### Step 2: OpenWeatherMap API Key

1. **Get API Key**
   - Sign up at: https://openweathermap.org/api
   - Navigate to API keys section
   - Copy your API key

2. **Configure API Key**
   - Open `src/main/resources/application.properties`
   - Update the API key:
   ```properties
   openweathermap.api.key=your_api_key_here
   ```

#### Step 3: Email Configuration (Optional)

1. **Gmail App Password Setup**
   - Enable 2-factor authentication on your Gmail account
   - Generate an app password: https://myaccount.google.com/apppasswords
   - Copy the generated password

2. **Update Email Settings**
   - Open `src/main/resources/application.properties`
   - Update email configuration:
   ```properties
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   ```

#### Step 4: Build Backend

```bash
# Navigate to project root
cd aqi-system-springboot-main

# Build the project
mvn clean install

# Or use Maven wrapper
./mvnw clean install  # Linux/Mac
mvnw.cmd clean install  # Windows
```

### Frontend Setup

#### Step 1: Install Dependencies

```bash
# Navigate to frontend directory
cd aqi-frontend

# Install npm packages
npm install
```

#### Step 2: Configure Backend URL (if needed)

The frontend is configured to connect to `http://localhost:8080` by default. If your backend runs on a different port, update the API calls in:
- `src/components/home.jsx` (line 61)

---

## Running the Application

### Starting the Backend

#### Option 1: Using Maven
```bash
# From project root
mvn spring-boot:run
```

#### Option 2: Using Maven Wrapper
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

#### Option 3: Using JAR File
```bash
# Build JAR first
mvn clean package

# Run JAR
java -jar target/aqisystem-0.0.1-SNAPSHOT.jar
```

**Backend will start on:** `http://localhost:8080`

**Verify backend is running:**
- Open browser: `http://localhost:8080/`
- You should see API information JSON

### Starting the Frontend

```bash
# Navigate to frontend directory
cd aqi-frontend

# Start development server
npm start
```

**Frontend will start on:** `http://localhost:3000`

The browser should automatically open. If not, manually navigate to `http://localhost:3000`

### Accessing the Application

1. **Frontend URL**: http://localhost:3000
2. **Backend API**: http://localhost:8080
3. **API Documentation**: http://localhost:8080/

---

## Core Features

### 1. Real-time AQI Monitoring

#### Description
The system provides real-time Air Quality Index monitoring for any city worldwide. It fetches current air quality data from OpenWeatherMap API and displays comprehensive information including AQI index, pollutant levels, and health recommendations.

#### Technical Implementation
- **API Endpoint**: OpenWeatherMap Air Pollution API
- **Data Retrieved**:
  - AQI Index (1-5 scale)
  - PM2.5, PM10 concentrations
  - CO, NO, NO2, O3, SO2, NH3 levels
  - Air quality category (Good, Fair, Moderate, Poor, Very Poor)

#### How It Works
1. User enters city name or uses GPS coordinates
2. System geocodes location to get coordinates
3. Fetches real-time air pollution data from OpenWeatherMap
4. Displays AQI index and pollutant breakdown
5. Provides health recommendations based on AQI level

#### User Interface
- **Dashboard Display**: Large AQI number with color-coded status
- **Pollutant Chart**: Bar chart showing individual pollutant levels
- **Health Recommendations**: Dynamic messages based on AQI level

#### AQI Scale
- **1 (Good)**: 0-50 - Safe for everyone
- **2 (Fair)**: 51-100 - Acceptable for most people
- **3 (Moderate)**: 101-150 - Unhealthy for sensitive groups
- **4 (Poor)**: 151-200 - Unhealthy for everyone
- **5 (Very Poor)**: 201+ - Hazardous conditions

---

### 2. Health Alerts and Notifications

#### Description
Automated health alert system that monitors air quality and sends notifications when AQI levels exceed safe thresholds. Users receive both in-app notifications and email alerts.

#### Technical Implementation
- **Scheduler**: Spring `@Scheduled` annotation
- **Frequency**: Runs every hour (3600000 ms)
- **Threshold**: AQI Index ≥ 101 (Moderate or worse)
- **Notification Methods**:
  - Database notifications (stored in `notifications` table)
  - Email alerts via SMTP

#### How It Works
1. **Scheduled Task** runs every hour
2. For each registered user:
   - Fetches latest AQI for user's preferred city
   - Checks if AQI ≥ 101
   - If threshold exceeded:
     - Creates notification record in database
     - Sends email alert to user
3. Users can view notifications in the application

#### Email Alert Content
- **Subject**: "🌤 AQI Update for [City Name]"
- **Body**: Health message based on AQI category
- **Categories**:
  - Good: "Air quality is Good. Enjoy your day!"
  - Fair: "Air quality is Fair. Sensitive groups should take care."
  - Moderate: "Air quality is Moderate. Consider limiting outdoor activity."
  - Poor: "Air quality is Poor. Better to stay indoors."
  - Very Poor: "Air quality is Very Poor. Avoid going outside!"

#### Configuration
- **Alert Threshold**: Configurable in `AQIWeatherScheduler.java` (line 38)
- **Email Settings**: Configured in `application.properties`
- **Scheduler Frequency**: Adjustable in `@Scheduled` annotation

---

### 3. Historical Data Storage and Visualization

#### Description
The system automatically stores all AQI readings in a MySQL database, allowing users to view historical trends and analyze air quality patterns over time.

#### Technical Implementation
- **Database Table**: `aqi_readings`
- **Storage Frequency**: Every time AQI is fetched
- **Data Stored**:
  - City information
  - AQI index and category
  - All pollutant levels (PM2.5, PM10, CO, NO, NO2, O3, SO2, NH3)
  - Timestamp of recording
  - Geographic coordinates

#### Database Schema
```sql
CREATE TABLE aqi_readings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_id BIGINT NOT NULL,
    category VARCHAR(50),
    pm25 DOUBLE,
    pm10 DOUBLE,
    co DOUBLE,
    no DOUBLE,
    no2 DOUBLE,
    o3 DOUBLE,
    so2 DOUBLE,
    nh3 DOUBLE,
    aqi_index INT,
    latitude DOUBLE,
    longitude DOUBLE,
    recorded_at DATETIME,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);
```

#### Data Retrieval Methods
- **Latest AQI**: `findTopByCityOrderByRecordedAtDesc()`
- **All Records**: `findByCityOrderByRecordedAtDesc()`
- **All Cities**: `findAllByOrderByRecordedAtDesc()`

#### Visualization
- **7-Day Trend Chart**: Line chart showing temperature trends (from forecast data)
- **Pollutant Bar Chart**: Visual representation of current pollutant levels
- **Historical Analysis**: Data available for trend analysis and reporting

#### Benefits
- Track air quality improvements/deteriorations
- Identify pollution patterns
- Generate reports for environmental studies
- Compare air quality across different time periods

---

### 4. Location-Based AQI Detection

#### Description
Users can automatically detect their current location using GPS and get instant AQI data for their exact location without manually entering city names.

#### Technical Implementation
- **Browser API**: HTML5 Geolocation API
- **Reverse Geocoding**: OpenWeatherMap Reverse Geocoding API
- **Coordinate Format**: Latitude, Longitude (e.g., "24.8607,67.0011")

#### How It Works
1. User clicks "Detect My Location" button
2. Browser requests GPS permission
3. System obtains latitude and longitude coordinates
4. Coordinates sent to backend as "lat,lon" string
5. Backend performs reverse geocoding to get city name
6. System fetches AQI data for that location
7. Results displayed on dashboard

#### Frontend Implementation
```javascript
const detectLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const { latitude, longitude } = pos.coords;
        fetchCityData(`${latitude},${longitude}`);
      },
      (err) => alert("Unable to get your location.")
    );
  }
};
```

#### Backend Processing
- **Coordinate Parsing**: Regex validation for "lat,lon" format
- **Reverse Geocoding**: Converts coordinates to city name
- **City Creation**: Automatically creates city record if not exists
- **Data Fetching**: Retrieves AQI and weather data

#### Privacy & Security
- GPS access requires user permission
- Coordinates only used for AQI lookup
- No location data stored permanently (unless user saves as favorite)

---

### 5. Air Quality Forecasting

#### Description
Provides 5-day air quality and weather forecasts to help users plan outdoor activities. The system displays temperature trends and weather conditions for the upcoming days.

#### Technical Implementation
- **API Endpoint**: OpenWeatherMap 5-Day Forecast API
- **Forecast Period**: 5 days (40 data points, 3-hour intervals)
- **Data Retrieved**:
  - Temperature (current and feels-like)
  - Humidity levels
  - Weather conditions (Clear, Clouds, Rain, etc.)
  - Weather icons
  - Date/time stamps

#### How It Works
1. User searches for a city
2. System fetches 5-day forecast from OpenWeatherMap
3. Forecast data parsed and structured
4. First 7 forecast items displayed as trend chart
5. Temperature trends visualized on line chart

#### Forecast Data Structure
```json
{
  "city": "Islamabad",
  "country": "PK",
  "forecastList": [
    {
      "dateTime": "2025-12-04 12:00:00",
      "temperature": 25.5,
      "feelsLike": 26.0,
      "humidity": 65,
      "weather": "Clear",
      "icon": "01d"
    },
    // ... more forecast items
  ]
}
```

#### Visualization
- **7-Day Temperature Trend**: Line chart showing temperature predictions
- **Forecast Items**: List of upcoming weather conditions
- **Planning Tool**: Helps users decide best times for outdoor activities

#### Use Cases
- Plan outdoor events based on air quality
- Schedule activities during better air quality days
- Avoid outdoor activities during poor air quality periods
- Track weather patterns alongside air quality

---

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### 1. Get AQI and Weather Data
**Endpoint**: `GET /api/data`

**Description**: Fetches real-time AQI, current weather, and forecast data for a specified city.

**Parameters**:
- `city` (required): City name or coordinates (format: "lat,lon")
- `units` (optional): Temperature units - "metric" (default) or "imperial"

**Request Example**:
```http
GET /api/data?city=Islamabad&units=metric
GET /api/data?city=24.8607,67.0011&units=metric
```

**Response Example**:
```json
{
  "aqi": {
    "id": 1,
    "city": {
      "id": 1,
      "name": "Islamabad",
      "country": "PK",
      "latitude": 33.6844,
      "longitude": 73.0479
    },
    "category": "Good",
    "pm25": 12.5,
    "pm10": 25.3,
    "co": 250.5,
    "no": 0.5,
    "no2": 15.2,
    "o3": 45.8,
    "so2": 5.2,
    "nh3": 2.1,
    "aqiIndex": 1,
    "latitude": 33.6844,
    "longitude": 73.0479,
    "recordedAt": "2025-12-03T22:00:00"
  },
  "currentWeather": {
    "city": "Islamabad",
    "country": "PK",
    "temperature": 25.5,
    "feelsLike": 26.0,
    "humidity": 65,
    "weather": "Clear",
    "icon": "01d"
  },
  "forecast": {
    "city": "Islamabad",
    "country": "PK",
    "forecastList": [
      {
        "dateTime": "2025-12-04 12:00:00",
        "temperature": 26.0,
        "feelsLike": 26.5,
        "humidity": 60,
        "weather": "Clear",
        "icon": "01d"
      }
      // ... more items
    ]
  },
  "cityName": "Islamabad",
  "cityId": 1,
  "coordinates": {
    "lat": 33.6844,
    "lon": 73.0479
  }
}
```

**Error Responses**:
- `400 Bad Request`: Missing city parameter
- `404 Not Found`: City not found or coordinates invalid
- `500 Internal Server Error`: API error or server issue

---

#### 2. Get All Users
**Endpoint**: `GET /api/users`

**Description**: Retrieves list of all registered users.

**Request Example**:
```http
GET /api/users
```

**Response Example**:
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "city": {
      "id": 1,
      "name": "Islamabad",
      "country": "PK"
    }
  }
]
```

---

#### 3. Register User
**Endpoint**: `POST /api/users/register`

**Description**: Registers a new user in the system.

**Request Body**:
```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "password123"
}
```

**Response Example**:
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "city": null
}
```

---

#### 4. Root Endpoint
**Endpoint**: `GET /`

**Description**: Returns API information and available endpoints.

**Response Example**:
```json
{
  "message": "AQI System API is running!",
  "status": "active",
  "version": "1.0.0",
  "availableEndpoints": {
    "GET /api/data": "Get AQI and weather data by city",
    "GET /api/users": "Get all users",
    "POST /api/users/register": "Register a new user"
  }
}
```

---

## Test Cases & Sample Inputs/Outputs

### Test Case 1: Real-time AQI Monitoring

#### Input
```
City: "Islamabad"
Units: "metric"
```

#### API Request
```http
GET http://localhost:8080/api/data?city=Islamabad&units=metric
```

#### Expected Output
```json
{
  "aqi": {
    "aqiIndex": 1,
    "category": "Good",
    "pm25": 12.5,
    "pm10": 25.3,
    "co": 250.5,
    "no2": 15.2,
    "o3": 45.8,
    "so2": 5.2
  },
  "currentWeather": {
    "city": "Islamabad",
    "temperature": 25.5,
    "humidity": 65,
    "weather": "Clear"
  },
  "forecast": {
    "city": "Islamabad",
    "forecastList": [...]
  }
}
```

#### Validation
- ✅ AQI index is between 1-5
- ✅ All pollutant values are non-negative
- ✅ Temperature is in Celsius (metric)
- ✅ City name matches input
- ✅ Forecast list contains data

---

### Test Case 2: Location-Based Detection

#### Input
```
Coordinates: "33.6844,73.0479" (Islamabad coordinates)
```

#### API Request
```http
GET http://localhost:8080/api/data?city=33.6844,73.0479&units=metric
```

#### Expected Output
```json
{
  "cityName": "Islamabad",
  "coordinates": {
    "lat": 33.6844,
    "lon": 73.0479
  },
  "aqi": {...},
  "currentWeather": {...}
}
```

#### Validation
- ✅ Coordinates correctly parsed
- ✅ Reverse geocoding returns correct city
- ✅ AQI data matches location
- ✅ Response includes city name

---

### Test Case 3: Invalid City Name

#### Input
```
City: "InvalidCityName123"
```

#### API Request
```http
GET http://localhost:8080/api/data?city=InvalidCityName123
```

#### Expected Output
```json
{
  "error": "City 'InvalidCityName123' not found. Please check the city name."
}
```

**Status Code**: `404 Not Found`

#### Validation
- ✅ Proper error message returned
- ✅ 404 status code
- ✅ No data returned

---

### Test Case 4: Missing City Parameter

#### Input
```
(No city parameter)
```

#### API Request
```http
GET http://localhost:8080/api/data
```

#### Expected Output
```json
{
  "error": "City name or coordinates are required."
}
```

**Status Code**: `400 Bad Request`

#### Validation
- ✅ Error message for missing parameter
- ✅ 400 status code

---

### Test Case 5: Health Alert Trigger

#### Test Scenario
1. User registered with preferred city: "Karachi"
2. Current AQI for Karachi: 105 (Moderate)
3. Scheduler runs (hourly)

#### Expected Behavior
- ✅ Notification created in database
- ✅ Email sent to user
- ✅ Notification message: "⚠ AQI Alert! The air quality for Karachi is 105 (Moderate)"

#### Database Check
```sql
SELECT * FROM notifications WHERE user_id = 1;
-- Should return notification record
```

---

### Test Case 6: Historical Data Storage

#### Test Scenario
1. Fetch AQI for "Lahore" multiple times
2. Check database for stored records

#### Database Query
```sql
SELECT * FROM aqi_readings 
WHERE city_id = (SELECT id FROM cities WHERE name = 'Lahore')
ORDER BY recorded_at DESC;
```

#### Expected Result
- ✅ Multiple records with different timestamps
- ✅ All pollutant values stored
- ✅ AQI index recorded
- ✅ Coordinates saved

---

### Test Case 7: Forecast Data Retrieval

#### Input
```
City: "Karachi"
```

#### API Request
```http
GET http://localhost:8080/api/data?city=Karachi
```

#### Expected Output
```json
{
  "forecast": {
    "city": "Karachi",
    "forecastList": [
      {
        "dateTime": "2025-12-04 00:00:00",
        "temperature": 28.5,
        "humidity": 70,
        "weather": "Clouds"
      },
      {
        "dateTime": "2025-12-04 03:00:00",
        "temperature": 27.0,
        "humidity": 75,
        "weather": "Clear"
      }
      // ... 38 more items (5 days, 3-hour intervals)
    ]
  }
}
```

#### Validation
- ✅ Forecast list contains 40 items (5 days × 8 intervals)
- ✅ Temperature values are reasonable
- ✅ DateTime format is correct
- ✅ Weather conditions are valid

---

### Test Case 8: Multiple Cities Comparison

#### Test Scenario
Fetch AQI for multiple cities and compare

#### Inputs
1. "Islamabad"
2. "Karachi"
3. "Lahore"

#### Expected Results
- ✅ Each city returns unique AQI values
- ✅ Coordinates differ for each city
- ✅ Data is city-specific (no cross-contamination)

---

### Test Case 9: CORS Configuration

#### Test Scenario
Frontend (localhost:3000) requests data from backend (localhost:8080)

#### Expected Behavior
- ✅ Request succeeds
- ✅ No CORS errors in browser console
- ✅ Response headers include CORS headers

#### Browser Console Check
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: *
Access-Control-Allow-Headers: *
```

---

### Test Case 10: Email Notification

#### Test Scenario
1. User with email registered
2. AQI exceeds threshold (≥101)
3. Scheduler triggers

#### Expected Behavior
- ✅ Email sent to user's email address
- ✅ Subject: "🌤 AQI Update for [City]"
- ✅ Body contains health message
- ✅ No email errors in logs

---

## User Manual

### Getting Started

#### Step 1: Access the Application
1. Open web browser (Chrome, Firefox, Safari, or Edge)
2. Navigate to: `http://localhost:3000`
3. You should see the AQI System homepage

#### Step 2: Sign Up / Sign In
1. Click "Sign Up" to create a new account
2. Enter your email and password
3. Click "Continue"
4. Or use "Sign In" if you already have an account

**Note**: Currently, authentication uses cookies. For production, implement proper authentication.

### Using the Dashboard

#### Searching for a City
1. **Manual Search**:
   - Type city name in the search box (e.g., "Islamabad", "Karachi")
   - Click "Search" button
   - Wait for data to load

2. **GPS Detection**:
   - Click "Detect My Location" button
   - Allow browser to access your location
   - System automatically fetches AQI for your location

#### Understanding the Dashboard

**AQI Display**:
- Large number shows current AQI index (1-5)
- Color-coded status:
  - 🟢 Green: Good (1)
  - 🟡 Yellow: Fair (2)
  - 🟠 Orange: Moderate (3)
  - 🔴 Red: Poor (4)
  - ⚫ Dark Red: Very Poor (5)

**Information Cards**:
- **Temperature**: Current temperature in Celsius
- **Humidity**: Current humidity percentage
- **PM2.5**: Fine particulate matter level (µg/m³)

**Charts**:
- **7-Day Temperature Trend**: Line chart showing forecasted temperatures
- **Pollutants**: Bar chart showing CO, NO2, SO2, O3 levels

**Health Recommendations**:
- Dynamic health messages based on AQI
- Color-coded recommendations
- List of safety tips

### Features Guide

#### 1. Map View
- Click "Map View" in sidebar
- See city location on interactive map
- Marker shows exact coordinates

#### 2. Favorites
- Click "Favorites" in sidebar
- Add current city to favorites (up to 4)
- Quick access to frequently checked cities
- Click on favorite to view its AQI

#### 3. Settings
- Click "Settings" in sidebar
- Update profile information
- Change password
- Upload profile picture
- Toggle dark/light theme

### Tips for Best Experience

1. **Allow Location Access**: For GPS detection to work
2. **Check Regularly**: AQI changes throughout the day
3. **Use Favorites**: Save frequently checked cities
4. **Monitor Trends**: Check forecast before planning outdoor activities
5. **Read Health Recommendations**: Follow safety guidelines based on AQI

### Troubleshooting Common Issues

#### Issue: "Backend is not reachable"
**Solution**: 
- Ensure backend is running on port 8080
- Check `http://localhost:8080/` in browser
- Verify no firewall blocking connection

#### Issue: "City not found"
**Solution**:
- Check spelling of city name
- Try using coordinates instead
- Ensure city exists in OpenWeatherMap database

#### Issue: GPS not working
**Solution**:
- Allow location permissions in browser
- Ensure HTTPS or localhost (required for geolocation)
- Check browser settings for location access

#### Issue: No data displayed
**Solution**:
- Check internet connection
- Verify OpenWeatherMap API key is valid
- Check browser console for errors
- Ensure backend is running

---

## Troubleshooting

### Backend Issues

#### Problem: Application won't start
**Possible Causes**:
1. Port 8080 already in use
2. Database connection failed
3. Missing dependencies

**Solutions**:
```bash
# Check if port is in use
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # Linux/Mac

# Change port in application.properties
server.port=8081

# Check database connection
mysql -u root -p
USE aqi_system;
```

#### Problem: Database connection error
**Solutions**:
1. Verify MySQL is running
2. Check credentials in `application.properties`
3. Ensure database exists: `CREATE DATABASE aqi_system;`
4. Check MySQL user permissions

#### Problem: API key errors
**Solutions**:
1. Verify OpenWeatherMap API key is valid
2. Check API key in `application.properties`
3. Ensure API key has required permissions
4. Check API quota/limits

### Frontend Issues

#### Problem: npm install fails
**Solutions**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### Problem: CORS errors
**Solutions**:
- Verify `CorsConfig.java` is present
- Check backend is running
- Ensure frontend URL matches CORS configuration

#### Problem: Charts not displaying
**Solutions**:
- Check browser console for errors
- Verify Recharts library is installed
- Ensure data format matches chart requirements

### Common Error Messages

#### "City not found"
- **Cause**: Invalid city name or API issue
- **Fix**: Try different city name or use coordinates

#### "Geocoding API returned no result"
- **Cause**: City name not recognized
- **Fix**: Use more specific city name or coordinates

#### "Cannot fetch AQI: missing coordinates"
- **Cause**: Geocoding failed
- **Fix**: Check internet connection and API key

---

## Future Enhancements

### Planned Features

1. **User Authentication**
   - JWT-based authentication
   - Password encryption
   - Session management

2. **Advanced Analytics**
   - Historical trend analysis
   - Comparative city analysis
   - Pollution source identification

3. **Mobile Application**
   - React Native mobile app
   - Push notifications
   - Offline mode

4. **Social Features**
   - Share AQI data on social media
   - Community reports
   - User reviews and tips

5. **Advanced Forecasting**
   - Machine learning predictions
   - Extended forecast (10+ days)
   - Weather pattern correlation

6. **Data Export**
   - PDF reports
   - CSV data export
   - API for third-party integration

7. **Multi-language Support**
   - Internationalization (i18n)
   - Multiple language options
   - Localized health messages

8. **Enhanced Notifications**
   - SMS alerts
   - Push notifications
   - Customizable alert thresholds

---

## Conclusion

The AQI System provides a comprehensive solution for air quality monitoring, combining real-time data, historical tracking, and predictive forecasting. The system is designed to be user-friendly, scalable, and extensible for future enhancements.

### Key Achievements
- ✅ Real-time AQI monitoring for any city
- ✅ Automated health alerts and notifications
- ✅ Historical data storage and analysis
- ✅ Location-based detection
- ✅ 5-day air quality forecasting
- ✅ Responsive web interface
- ✅ RESTful API architecture

### Technical Highlights
- Modern tech stack (Spring Boot + React)
- Clean architecture and code organization
- Comprehensive error handling
- CORS-enabled for frontend integration
- Automated scheduling for alerts
- Database persistence for historical data

---

## Appendix

### Database Schema

#### Cities Table
```sql
CREATE TABLE cities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(10),
    latitude DOUBLE,
    longitude DOUBLE
);
```

#### AQI Readings Table
```sql
CREATE TABLE aqi_readings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_id BIGINT NOT NULL,
    category VARCHAR(50),
    pm25 DOUBLE,
    pm10 DOUBLE,
    co DOUBLE,
    no DOUBLE,
    no2 DOUBLE,
    o3 DOUBLE,
    so2 DOUBLE,
    nh3 DOUBLE,
    aqi_index INT,
    latitude DOUBLE,
    longitude DOUBLE,
    recorded_at DATETIME,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);
```

#### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255),
    city_id BIGINT,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);
```

#### Notifications Table
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    city_id BIGINT,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (city_id) REFERENCES cities(id)
);
```

### API Rate Limits
- **OpenWeatherMap Free Tier**: 60 calls/minute
- **Recommended**: Implement caching to reduce API calls
- **Production**: Consider upgrading to paid tier for higher limits

### Security Considerations
- API keys should be stored securely (use environment variables)
- Implement proper authentication and authorization
- Use HTTPS in production
- Sanitize user inputs
- Implement rate limiting for API endpoints

### Performance Optimization
- Implement caching for frequently accessed data
- Use database indexing for faster queries
- Optimize API calls (batch requests where possible)
- Implement pagination for large datasets
- Use CDN for static assets

---

**Document Version**: 1.0  
**Last Updated**: December 2025  
**Author**: AQI System Development Team

