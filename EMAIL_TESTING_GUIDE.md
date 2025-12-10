# Email Notification System - Testing Guide

This guide provides multiple ways to test if the email notification system is working correctly.

## Prerequisites

1. **Backend is running** on `http://localhost:8080`
2. **Email configuration** is set in `application.properties`:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```
3. **Database has users** with:
   - Valid email addresses
   - Default cities set (city_id in users table)

---

## Testing Methods

### Method 1: Simple Email Test (Recommended First Step)

Test if the email service can send emails at all.

**Endpoint:**
```
GET http://localhost:8080/api/test/email?to=your-email@example.com
```

**Example using curl:**
```bash
curl "http://localhost:8080/api/test/email?to=your-email@example.com"
```

**Example using browser:**
```
http://localhost:8080/api/test/email?to=your-email@example.com
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Test email sent successfully to: your-email@example.com",
  "timestamp": "2025-12-04T10:30:00"
}
```

**What to check:**
- ✅ Check your email inbox (and spam folder)
- ✅ You should receive a test email with subject "🧪 Test Email from AQI System"

---

### Method 2: Test Alert for Specific User

Manually trigger the alert system for a specific user (simulates what the scheduler does).

**Endpoint:**
```
GET http://localhost:8080/api/test/trigger-alert?userId=1
```

**Example using curl:**
```bash
curl "http://localhost:8080/api/test/trigger-alert?userId=1"
```

**Expected Response (if AQI >= threshold):**
```json
{
  "success": true,
  "userEmail": "user@example.com",
  "city": "Karachi",
  "currentAQI": 105,
  "aqiCategory": "Moderate",
  "threshold": 101,
  "exceedsThreshold": true,
  "alertSent": true,
  "notificationCreated": true,
  "emailSent": true,
  "message": "Alert sent successfully!",
  "timestamp": "2025-12-04T10:30:00"
}
```

**Expected Response (if AQI < threshold):**
```json
{
  "success": true,
  "userEmail": "user@example.com",
  "city": "Islamabad",
  "currentAQI": 45,
  "aqiCategory": "Good",
  "threshold": 101,
  "exceedsThreshold": false,
  "alertSent": false,
  "message": "AQI is within safe range. No alert sent.",
  "timestamp": "2025-12-04T10:30:00"
}
```

**What to check:**
- ✅ If AQI >= threshold, check email inbox for health alert
- ✅ Check `notifications` table in database for new notification record
- ✅ Verify email contains health recommendations and pollutant levels

---

### Method 3: Test All Users (Simulate Scheduler)

Manually trigger alerts for all users (simulates the hourly scheduler run).

**Endpoint:**
```
GET http://localhost:8080/api/test/trigger-all-alerts
```

**Example using curl:**
```bash
curl "http://localhost:8080/api/test/trigger-all-alerts"
```

**Expected Response:**
```json
{
  "success": true,
  "totalUsers": 5,
  "processedUsers": 4,
  "alertsSent": 2,
  "errorsEncountered": 0,
  "timestamp": "2025-12-04T10:30:00"
}
```

**What to check:**
- ✅ Check console logs for detailed processing information
- ✅ Check email inboxes for all users with AQI >= threshold
- ✅ Check `notifications` table for new records

---

### Method 4: Check Email Configuration

Verify that email service is properly configured.

**Endpoint:**
```
GET http://localhost:8080/api/test/email-config
```

**Example using curl:**
```bash
curl "http://localhost:8080/api/test/email-config"
```

**Expected Response:**
```json
{
  "emailServiceAvailable": true,
  "message": "Email service is configured. Use /api/test/email to test sending.",
  "timestamp": "2025-12-04T10:30:00"
}
```

---

### Method 5: Wait for Automatic Scheduler

The scheduler runs automatically every hour. To test this:

1. **Start the application** and wait for the scheduler to run
2. **Check console logs** - you should see:
   ```
   === AQI Health Alert Scheduler Started at 2025-12-04T10:00:00 ===
   Processing 5 user(s) for AQI alerts...
   Checking AQI for user: user@example.com in city: Karachi
   Current AQI for Karachi: 105 (Moderate)
   Notification created for user: user@example.com
   Email alert sent to: user@example.com - AQI: 105 (Threshold: 101)
   === Scheduler Completed ===
   Alerts sent: 2
   Errors encountered: 0
   ```

3. **Check email inboxes** for users with AQI >= threshold
4. **Check database** `notifications` table for new records

**Note:** To test immediately without waiting, use Method 2 or 3 above.

---

## Database Verification

### Check Users Table
```sql
SELECT id, email, name, city_id 
FROM users 
WHERE email IS NOT NULL AND city_id IS NOT NULL;
```

### Check Notifications Table
```sql
SELECT * FROM notifications 
ORDER BY created_at DESC 
LIMIT 10;
```

### Check AQI Readings
```sql
SELECT a.*, c.name as city_name 
FROM aqi_readings a 
JOIN cities c ON a.city_id = c.id 
ORDER BY a.recorded_at DESC 
LIMIT 10;
```

---

## Troubleshooting

### Issue: Email not received

**Possible causes:**
1. **Email in spam folder** - Check spam/junk folder
2. **Invalid email configuration** - Verify `application.properties` settings
3. **Gmail app password not set** - You need to use an app password, not your regular password
4. **Email service error** - Check console logs for error messages

**Solution:**
- Test with Method 1 first to isolate the issue
- Check console logs for detailed error messages
- Verify Gmail app password is correct

### Issue: "User has no default city set"

**Solution:**
```sql
-- Set a default city for a user
UPDATE users 
SET city_id = (SELECT id FROM cities WHERE name = 'Karachi' LIMIT 1) 
WHERE id = 1;
```

### Issue: "Failed to fetch AQI"

**Possible causes:**
1. OpenWeatherMap API key invalid or expired
2. City coordinates missing
3. Network connectivity issues

**Solution:**
- Verify API key in `application.properties`
- Check if city has latitude/longitude in database
- Check console logs for API errors

### Issue: Email sent but AQI doesn't exceed threshold

**Solution:**
- This is expected behavior - emails are only sent when AQI >= threshold
- To test with lower threshold, temporarily change `aqi.alert.threshold` in `application.properties`
- Or use Method 2 to see current AQI value

---

## Testing Checklist

- [ ] Test simple email sending (Method 1)
- [ ] Verify email received in inbox
- [ ] Test alert for specific user (Method 2)
- [ ] Verify notification created in database
- [ ] Verify email contains health recommendations
- [ ] Test with user having AQI < threshold (should not send)
- [ ] Test with user having AQI >= threshold (should send)
- [ ] Check console logs for detailed information
- [ ] Verify scheduler runs automatically (Method 5)
- [ ] Test all users alert trigger (Method 3)

---

## Quick Test Script

Here's a quick test sequence:

1. **Test email service:**
   ```bash
   curl "http://localhost:8080/api/test/email?to=your-email@example.com"
   ```

2. **Check if user exists and has city:**
   ```bash
   curl "http://localhost:8080/api/users"
   ```

3. **Trigger alert for user ID 1:**
   ```bash
   curl "http://localhost:8080/api/test/trigger-alert?userId=1"
   ```

4. **Check notifications in database:**
   ```sql
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5;
   ```

---

## Email Content Verification

When you receive an email alert, verify it contains:

- ✅ Subject: "⚠ AQI Health Alert for [City Name]"
- ✅ Personalized greeting (if user name is set)
- ✅ Current AQI value and category
- ✅ Health recommendations based on AQI level
- ✅ Pollutant levels (PM2.5, PM10, CO, NO2, O3)
- ✅ Safety tips and recommendations

---

## Notes

- The scheduler runs every hour automatically
- Emails are only sent when AQI >= threshold (default: 101)
- Notifications are always created in database when threshold is exceeded
- Test endpoints are available for development/testing purposes
- In production, consider removing or securing test endpoints

