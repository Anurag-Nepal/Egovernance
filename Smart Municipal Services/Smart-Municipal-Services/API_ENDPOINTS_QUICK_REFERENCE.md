# API Endpoints Quick Reference

## 🔐 Authentication Endpoints (Public - No Auth Required)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | ❌ No |
| POST | `/api/auth/verify-otp` | Verify OTP code | ❌ No |
| POST | `/api/auth/login` | User login | ❌ No |
| POST | `/api/auth/resend-otp?email={email}` | Resend OTP | ❌ No |

---

## 📋 Complaint Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/api/complaints/raise` | Raise complaint | ✅ Yes | USER |
| GET | `/api/complaints?status=&type=&date=` | Get complaints | ✅ Yes | USER |
| PUT | `/api/complaints/{id}/complete` | Mark as completed | ✅ Yes | ADMIN |
| DELETE | `/api/complaints/{id}` | Delete complaint | ✅ Yes | ADMIN |

---

## 💬 Feedback Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/api/feedback` | Submit feedback | ✅ Yes | USER |
| GET | `/api/feedback/rating` | Get overall rating | ❌ No | Public |

---

## 📄 Document Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/api/documents/request` | Request document | ✅ Yes | USER |
| GET | `/api/documents/verify?docId=&hash=` | Verify document QR | ❌ No | Public |
| POST | `/api/documents/issue/{applicationId}` | Issue document | ✅ Yes | ADMIN |

---

## 📝 Complete Workflow Examples

### 1. New User Registration & First Complaint

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"John Doe","email":"john@example.com","username":"johndoe","password":"password123"}'

# 2. Check email for OTP (e.g., 654321)

# 3. Verify OTP
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","otp":"654321"}'

# 4. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"password123"}'

# 5. Raise Complaint (authenticated)
curl -X POST http://localhost:8080/api/complaints/raise \
  -u johndoe:password123 \
  -F "complaint={\"complaintType\":\"ROAD\",\"complaintTitle\":\"Pothole\",\"message\":\"Big pothole\"};type=application/json" \
  -F "image=@photo.jpg"

# 6. Submit Feedback
curl -X POST http://localhost:8080/api/feedback \
  -u johndoe:password123 \
  -H "Content-Type: application/json" \
  -d '{"complaintId":1,"description":"Fixed quickly!","rating":5}'
```

---

## 🔑 Authentication Methods

### Option 1: HTTP Basic Auth (For Testing)
```bash
curl -u username:password http://localhost:8080/api/complaints
```

### Option 2: Authorization Header
```bash
curl -H "Authorization: Basic $(echo -n username:password | base64)" \
  http://localhost:8080/api/complaints
```

### Option 3: Use Token from Login Response
After login, use the token from the response:
```bash
TOKEN="am9obmRvZTpwYXNzd29yZDEyMw=="
curl -H "Authorization: Basic $TOKEN" http://localhost:8080/api/complaints
```

---

## 📊 Response Format

All endpoints return JSON in this format:

```json
{
  "status": 200,
  "message": "Success message or error message",
  "data": {
    // Response data (can be null)
  }
}
```

---

## ⚡ Quick Test Commands

### Test Public Endpoints (No Auth)
```bash
# Get overall rating
curl http://localhost:8080/api/feedback/rating

# Verify document
curl "http://localhost:8080/api/documents/verify?docId=1&hash=abc123"
```

### Test with Authentication
```bash
# Get complaints (as user)
curl -u johndoe:password123 http://localhost:8080/api/complaints

# Complete complaint (as admin)
curl -X PUT -u admin:adminpass http://localhost:8080/api/complaints/1/complete
```

---

## 🎯 Common Use Cases

### Use Case 1: Citizen Reports Pothole
1. Register account → Verify OTP → Login
2. POST `/api/complaints/raise` with photo
3. Check status: GET `/api/complaints?status=GENERATED`

### Use Case 2: Citizen Requests Birth Certificate
1. Login with credentials
2. POST `/api/documents/request` with details
3. Admin issues: POST `/api/documents/issue/{id}`
4. Citizen receives document via email

### Use Case 3: Verify Document Authenticity
1. Scan QR code on document
2. GET `/api/documents/verify?docId=...&hash=...`
3. Get verification result (public endpoint)

---

## 🛠️ Testing with Postman

### Collection Structure:
```
Smart Municipal Services
├── Auth
│   ├── Register
│   ├── Verify OTP
│   ├── Login
│   └── Resend OTP
├── Complaints
│   ├── Raise Complaint
│   ├── Get Complaints
│   ├── Complete Complaint (Admin)
│   └── Delete Complaint (Admin)
├── Feedback
│   ├── Submit Feedback
│   └── Get Rating (Public)
└── Documents
    ├── Request Document
    ├── Verify Document (Public)
    └── Issue Document (Admin)
```

---

**Last Updated**: December 30, 2025

