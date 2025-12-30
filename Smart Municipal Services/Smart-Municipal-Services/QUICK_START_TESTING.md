# 🚀 QUICK START TESTING GUIDE

## Step-by-Step Testing Instructions

### Prerequisites
✅ MySQL running on localhost:3306  
✅ Database 'egovernance' created  
✅ Application started: `mvn spring-boot:run`  

---

## 🧪 Test Sequence

### 1️⃣ REGISTER A NEW USER

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "your-email@gmail.com",
    "username": "johndoe",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Registration successful! Please check your email for OTP verification code.",
  "data": null
}
```

**⏰ Action Required**: Check your email for OTP code

---

### 2️⃣ VERIFY OTP

```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@gmail.com",
    "otp": "YOUR_OTP_FROM_EMAIL"
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Email verified successfully! You can now login.",
  "data": null
}
```

---

### 3️⃣ LOGIN

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "token": "am9obmRvZTpwYXNzd29yZDEyMw==",
    "username": "johndoe",
    "email": "your-email@gmail.com",
    "role": "USER",
    "message": "Login successful"
  }
}
```

---

### 4️⃣ RAISE A COMPLAINT (Authenticated)

```bash
curl -X POST http://localhost:8080/api/complaints/raise \
  -u johndoe:password123 \
  -F 'complaint={"complaintType":"ROAD","complaintTitle":"Pothole on Main Street","message":"Large pothole causing issues"};type=application/json' \
  -F "image=@/path/to/your/image.jpg"
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Complaint raised successfully",
  "data": "Complaint raised successfully with ID: 1"
}
```

---

### 5️⃣ VIEW COMPLAINTS

```bash
curl -X GET http://localhost:8080/api/complaints \
  -u johndoe:password123
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Complaint retrieved successfully",
  "data": {
    "id": 1,
    "complaintType": "ROAD",
    "complaintTitle": "Pothole on Main Street",
    "message": "Large pothole causing issues",
    "generatedAt": "2025-12-30T06:00:00",
    "complaintStatus": "GENERATED"
  }
}
```

---

### 6️⃣ SUBMIT FEEDBACK

```bash
curl -X POST http://localhost:8080/api/feedback \
  -u johndoe:password123 \
  -H "Content-Type: application/json" \
  -d '{
    "complaintId": 1,
    "description": "The pothole was fixed quickly. Great work!",
    "rating": 5
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Feedback created successfully for complaint ID: 1",
  "data": null
}
```

---

### 7️⃣ CHECK OVERALL RATING (Public - No Auth)

```bash
curl -X GET http://localhost:8080/api/feedback/rating
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Overall rating retrieved successfully",
  "data": {
    "complaintId": 0,
    "rating": 5
  }
}
```

---

## 🔧 Troubleshooting

### Issue: OTP email not received
**Solutions:**
1. Check spam/junk folder
2. Verify email configuration in `application.properties`
3. Use resend OTP endpoint:
```bash
curl -X POST "http://localhost:8080/api/auth/resend-otp?email=your-email@gmail.com"
```

### Issue: Login fails with "Account not activated"
**Solution:** You must verify OTP first (Step 2)

### Issue: 401 Unauthorized on authenticated endpoints
**Solution:** Make sure you're using correct credentials with `-u username:password`

### Issue: Port 8080 already in use
**Solution:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /F /PID <PID>

# Or change port in application.properties
server.port=8081
```

---

## 📱 Testing with Postman

### Import These Requests:

**1. Register**
- Method: POST
- URL: `http://localhost:8080/api/auth/register`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "fullName": "John Doe",
  "email": "your-email@gmail.com",
  "username": "johndoe",
  "password": "password123"
}
```

**2. Verify OTP**
- Method: POST
- URL: `http://localhost:8080/api/auth/verify-otp`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "email": "your-email@gmail.com",
  "otp": "123456"
}
```

**3. Login**
- Method: POST
- URL: `http://localhost:8080/api/auth/login`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**4. Raise Complaint**
- Method: POST
- URL: `http://localhost:8080/api/complaints/raise`
- Authorization: Basic Auth (username: johndoe, password: password123)
- Body: form-data
  - Key: `complaint` (JSON), Value: `{"complaintType":"ROAD","complaintTitle":"Test","message":"Test message"}`
  - Key: `image` (File), Value: Select an image file

---

## ✅ Success Indicators

| Step | Success Indicator |
|------|-------------------|
| Registration | Status 200, "Registration successful" message |
| Email | Receive 6-digit OTP in email inbox |
| OTP Verification | Status 200, "Email verified successfully" |
| Login | Status 200, receive token and user details |
| Raise Complaint | Status 200, receive complaint ID |
| View Complaints | Status 200, see complaint details |
| Submit Feedback | Status 200, "Feedback created successfully" |

---

## 🎯 Valid Values Reference

### Complaint Types
- `ROAD`
- `WATER`
- `ELECTRICITY`
- `SANITATION`
- `STREETLIGHT`
- `DRAINAGE`
- `OTHER`

### Complaint Status
- `GENERATED` (initial)
- `IN_PROGRESS`
- `COMPLETED`

### Feedback Rating
- Values: 1 to 5 (integer)

### User Roles
- `USER` (default for registration)
- `ADMIN` (requires database update)

---

## 🔐 Create Admin User (SQL)

To test admin endpoints, create an admin user in database:

```sql
-- First register normally, then update role
UPDATE user 
SET role = 'ADMIN' 
WHERE username = 'johndoe';
```

Then test admin endpoints:

```bash
# Mark complaint as complete (Admin only)
curl -X PUT http://localhost:8080/api/complaints/1/complete \
  -u johndoe:password123

# Delete complaint (Admin only)
curl -X DELETE http://localhost:8080/api/complaints/1 \
  -u johndoe:password123
```

---

## 📊 Database Verification

Check if everything is working:

```sql
-- Check registered users
SELECT id, username, email, enabled, email_verified, role FROM user;

-- Check OTP records
SELECT email, otp, verified, created_at, expires_at FROM otp_verification;

-- Check complaints
SELECT id, complaint_type, complaint_title, complaint_status FROM complaint;

-- Check feedback
SELECT id, rating, description_of_feedback FROM feedback;
```

---

## ⚡ Quick Commands Reference

```bash
# Start app
mvn spring-boot:run

# Build
mvn clean compile

# Check port
netstat -ano | findstr :8080

# View logs
tail -f logs/application.log  # if configured

# Stop app
Ctrl + C
```

---

## 🎉 You're All Set!

Follow the 7 steps above in sequence and you'll have:
1. ✅ A registered and verified user account
2. ✅ A raised complaint with photo
3. ✅ Feedback submitted
4. ✅ Overall rating calculated

**Happy Testing!** 🚀


