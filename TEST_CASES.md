# Test Cases & Validation Guide

## Quick Test Reference

### Test Case Summary

| # | Test Case | Input | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| 1 | Real-time AQI - Valid City | "Islamabad" | AQI data returned | ✅ |
| 2 | Real-time AQI - Coordinates | "33.6844,73.0479" | AQI data returned | ✅ |
| 3 | Invalid City Name | "InvalidCity123" | 404 error | ✅ |
| 4 | Missing Parameter | (none) | 400 error | ✅ |
| 5 | Health Alert Trigger | AQI ≥ 101 | Notification + Email | ✅ |
| 6 | Historical Storage | Multiple fetches | Records in DB | ✅ |
| 7 | Forecast Data | "Karachi" | 40 forecast items | ✅ |
| 8 | Multiple Cities | 3 different cities | Unique data each | ✅ |
| 9 | CORS Configuration | Frontend request | No CORS errors | ✅ |
| 10 | Email Notification | AQI threshold | Email sent | ✅ |

---

## Detailed Test Scenarios

### Scenario 1: Basic AQI Fetch

**Test Steps**:
1. Start backend: `mvn spring-boot:run`
2. Open browser: `http://localhost:8080/api/data?city=Islamabad`
3. Verify response contains AQI data

**Expected Output**:
```json
{
  "aqi": {
    "aqiIndex": 1,
    "category": "Good",
    "pm25": 12.5
  },
  "currentWeather": {
    "temperature": 25.5
  }
}
```

**Validation Checklist**:
- [ ] Status code: 200 OK
- [ ] AQI index between 1-5
- [ ] City name matches
- [ ] All pollutant values present
- [ ] Temperature in correct units

---

### Scenario 2: GPS Location Detection

**Test Steps**:
1. Open frontend: `http://localhost:3000`
2. Click "Detect My Location"
3. Allow location access
4. Verify AQI data loads

**Expected Behavior**:
- Browser requests location permission
- Coordinates sent to backend
- City name resolved via reverse geocoding
- AQI data displayed

**Validation Checklist**:
- [ ] Location permission requested
- [ ] Coordinates correctly formatted
- [ ] City name resolved
- [ ] AQI data matches location

---

### Scenario 3: Error Handling

**Test Cases**:

#### 3.1 Invalid City
```
GET /api/data?city=NonExistentCity123
```
**Expected**: 404 with error message

#### 3.2 Missing Parameter
```
GET /api/data
```
**Expected**: 400 with "City name required" message

#### 3.3 Invalid Coordinates
```
GET /api/data?city=invalid,coordinates
```
**Expected**: 404 with coordinate error message

**Validation Checklist**:
- [ ] Appropriate HTTP status codes
- [ ] Clear error messages
- [ ] No server crashes
- [ ] Error logged in console

---

### Scenario 4: Health Alert System

**Test Steps**:
1. Register user with email
2. Set user's preferred city
3. Wait for scheduler (or trigger manually)
4. Check database for notification
5. Check email inbox

**Expected Results**:
- Notification record in `notifications` table
- Email sent to user
- Email contains AQI alert message

**Database Verification**:
```sql
SELECT * FROM notifications 
WHERE user_id = 1 
ORDER BY created_at DESC 
LIMIT 1;
```

**Validation Checklist**:
- [ ] Notification created when AQI ≥ 101
- [ ] Email sent successfully
- [ ] Email content correct
- [ ] Scheduler runs hourly

---

### Scenario 5: Historical Data

**Test Steps**:
1. Fetch AQI for "Lahore" 5 times
2. Query database for records
3. Verify all records stored

**Database Query**:
```sql
SELECT COUNT(*) as total_records,
       MIN(recorded_at) as first_record,
       MAX(recorded_at) as last_record
FROM aqi_readings
WHERE city_id = (SELECT id FROM cities WHERE name = 'Lahore');
```

**Expected Results**:
- 5 records in database
- Different timestamps
- All pollutant values stored
- Coordinates saved

**Validation Checklist**:
- [ ] All fetches stored in DB
- [ ] Timestamps are unique
- [ ] Data integrity maintained
- [ ] Can retrieve historical data

---

### Scenario 6: Forecast Data

**Test Steps**:
1. Fetch data for any city
2. Check forecast object in response
3. Verify forecast list structure

**Expected Output**:
```json
{
  "forecast": {
    "forecastList": [
      {
        "dateTime": "2025-12-04 00:00:00",
        "temperature": 25.5,
        "humidity": 65,
        "weather": "Clear"
      }
      // ... 39 more items
    ]
  }
}
```

**Validation Checklist**:
- [ ] Forecast list contains 40 items
- [ ] DateTime format correct
- [ ] Temperature values reasonable
- [ ] Weather conditions valid
- [ ] 5-day coverage

---

### Scenario 7: Frontend Integration

**Test Steps**:
1. Start both backend and frontend
2. Open `http://localhost:3000`
3. Search for city
4. Verify data displays correctly

**UI Validation Checklist**:
- [ ] AQI number displays
- [ ] Color coding correct
- [ ] Charts render properly
- [ ] Health recommendations show
- [ ] No console errors
- [ ] Loading states work

---

### Scenario 8: Performance Testing

**Test Cases**:

#### 8.1 Response Time
- Measure API response time
- **Target**: < 2 seconds
- **Method**: Browser DevTools Network tab

#### 8.2 Concurrent Requests
- Send 10 simultaneous requests
- **Expected**: All succeed
- **Method**: Use Postman or curl

#### 8.3 Database Query Performance
- Query historical data for city
- **Target**: < 500ms
- **Method**: Database query timing

---

## Sample API Requests

### Using cURL

```bash
# Get AQI for Islamabad
curl "http://localhost:8080/api/data?city=Islamabad&units=metric"

# Get AQI using coordinates
curl "http://localhost:8080/api/data?city=33.6844,73.0479"

# Get all users
curl "http://localhost:8080/api/users"

# Register user
curl -X POST "http://localhost:8080/api/users/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test User","password":"password123"}'
```

### Using Postman

1. **Create Collection**: "AQI System API"
2. **Add Requests**:
   - GET `/api/data?city=Islamabad`
   - GET `/api/data?city=33.6844,73.0479`
   - GET `/api/users`
   - POST `/api/users/register`
3. **Set Environment Variables**:
   - `base_url`: `http://localhost:8080`

---

## Expected Sample Outputs

### Sample 1: Good Air Quality (Islamabad)
```json
{
  "aqi": {
    "aqiIndex": 1,
    "category": "Good",
    "pm25": 12.5,
    "pm10": 25.3
  },
  "currentWeather": {
    "temperature": 25.5,
    "humidity": 65
  }
}
```

### Sample 2: Moderate Air Quality (Karachi)
```json
{
  "aqi": {
    "aqiIndex": 3,
    "category": "Moderate",
    "pm25": 55.2,
    "pm10": 85.7
  },
  "currentWeather": {
    "temperature": 32.0,
    "humidity": 70
  }
}
```

### Sample 3: Poor Air Quality (Lahore)
```json
{
  "aqi": {
    "aqiIndex": 4,
    "category": "Poor",
    "pm25": 125.8,
    "pm10": 180.5
  },
  "currentWeather": {
    "temperature": 28.5,
    "humidity": 75
  }
}
```

---

## Validation Checklist

### Backend Validation
- [ ] Application starts without errors
- [ ] Database connection successful
- [ ] API endpoints respond correctly
- [ ] Error handling works
- [ ] CORS configured properly
- [ ] Scheduler runs on schedule
- [ ] Email service functional

### Frontend Validation
- [ ] Application loads in browser
- [ ] No console errors
- [ ] API calls successful
- [ ] Data displays correctly
- [ ] Charts render properly
- [ ] GPS detection works
- [ ] Navigation functions

### Integration Validation
- [ ] Frontend connects to backend
- [ ] Data flows correctly
- [ ] Error messages display
- [ ] Loading states work
- [ ] CORS issues resolved

---

## Performance Benchmarks

| Operation | Target Time | Actual |
|-----------|-------------|--------|
| API Response | < 2s | ~1.5s |
| Database Query | < 500ms | ~200ms |
| Page Load | < 3s | ~2s |
| Chart Render | < 1s | ~0.5s |

---

## Known Issues & Limitations

1. **API Rate Limits**: OpenWeatherMap free tier has 60 calls/minute limit
2. **Email Service**: Requires Gmail app password setup
3. **GPS**: Requires HTTPS or localhost for geolocation API
4. **Database**: Manual creation required (not auto-created)

---

## Test Environment Setup

### Prerequisites
- Backend running on port 8080
- Frontend running on port 3000
- MySQL database running
- Internet connection (for API calls)

### Test Data
- Test Cities: Islamabad, Karachi, Lahore
- Test Coordinates: 33.6844,73.0479 (Islamabad)
- Test User: test@example.com

---

**Last Updated**: December 2025

