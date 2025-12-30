# Spring Security Configuration - API Authentication Guide

## 🔒 Security Configuration Updated

The Spring Security has been configured to properly handle API endpoints with appropriate access controls.

---

## 📋 Endpoint Access Control

### ✅ Public Endpoints (No Authentication Required)
- `GET /api/documents/verify` - QR code document verification
- `GET /api/feedback/rating` - View overall rating statistics
- `POST /login` - User login
- `POST /register` - User registration

### 🔐 Authenticated User Endpoints
- `POST /api/complaints/raise` - Raise a new complaint
- `GET /api/complaints` - Get complaints with filters
- `POST /api/feedback` - Submit feedback
- `POST /api/documents/request` - Request a document

### 👑 Admin Only Endpoints
- `PUT /api/complaints/{id}/complete` - Mark complaint as completed
- `DELETE /api/complaints/{id}` - Delete a complaint
- `POST /api/documents/issue/{applicationId}` - Issue a document

---

## 🚀 How to Test the API

### Option 1: Using Postman with Basic Auth

#### Step 1: Create a User First
You need to create a user in the database. You can either:

1. **Using SQL directly:**
```sql
INSERT INTO user (full_name, email, username, password, role) 
VALUES ('Test User', 'test@example.com', 'testuser', 
        '$2a$10$YOUR_BCRYPT_HASHED_PASSWORD', 'USER');
```

2. **Or create a registration endpoint** (if you have one implemented)

#### Step 2: Test in Postman

**For Authenticated Endpoints:**
1. Open Postman
2. Select your request (e.g., POST to `/api/complaints/raise`)
3. Go to **Authorization** tab
4. Select **Type**: `Basic Auth`
5. Enter:
   - **Username**: `testuser`
   - **Password**: `yourpassword`
6. Set the request body and send

**Example Request:**
```
POST http://localhost:8080/api/complaints/raise
Authorization: Basic Auth
  Username: testuser
  Password: yourpassword

Body (form-data):
  complaint: {"complaintType":"ROAD","complaintTitle":"Test","message":"Test message"}
  image: [select file]
```

---

### Option 2: Using cURL with Basic Auth

```bash
# Raise a complaint
curl -X POST http://localhost:8080/api/complaints/raise \
  -u testuser:yourpassword \
  -F "complaint={\"complaintType\":\"ROAD\",\"complaintTitle\":\"Test\",\"message\":\"Test message\"};type=application/json" \
  -F "image=@/path/to/image.jpg"

# Get complaints
curl -X GET "http://localhost:8080/api/complaints?status=GENERATED" \
  -u testuser:yourpassword

# Create feedback
curl -X POST http://localhost:8080/api/feedback \
  -u testuser:yourpassword \
  -H "Content-Type: application/json" \
  -d '{"complaintId":1,"description":"Good work","rating":5}'
```

---

### Option 3: Test Public Endpoints (No Auth Needed)

```bash
# Verify document (public)
curl -X GET "http://localhost:8080/api/documents/verify?docId=123&hash=abc123"

# Get overall rating (public)
curl -X GET http://localhost:8080/api/feedback/rating
```

---

## 🔧 Creating Test Users

### Method 1: SQL Script

Create a file `create-test-users.sql`:

```sql
-- Password is 'password123' hashed with BCrypt
INSERT INTO user (full_name, email, username, password, role) VALUES
('Regular User', 'user@test.com', 'user', 
 '$2a$10$rBV2/L8T5.KxTgXEbfKF7eWNbVPLdNVfQQp8kKCWdZ3Fn2qPdq0Cy', 'USER'),
 
('Admin User', 'admin@test.com', 'admin', 
 '$2a$10$rBV2/L8T5.KxTgXEbfKF7eWNbVPLdNVfQQp8kKCWdZ3Fn2qPdq0Cy', 'ADMIN');
```

Run it:
```bash
mysql -u root -p egovernance < create-test-users.sql
```

### Method 2: Use BCrypt Password Generator

If you need to generate BCrypt passwords, you can use this Java code:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hashed = encoder.encode(password);
        System.out.println("Hashed password: " + hashed);
    }
}
```

Or use an online tool: https://bcrypt-generator.com/

---

## 📝 Testing Workflow

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Create Test Users
```sql
-- Run in MySQL
USE egovernance;

INSERT INTO user (full_name, email, username, password, role) VALUES
('Test User', 'user@test.com', 'user', 
 '$2a$10$rBV2/L8T5.KxTgXEbfKF7eWNbVPLdNVfQQp8kKCWdZ3Fn2qPdq0Cy', 'USER');
```

### 3. Test Public Endpoint (No Auth)
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
    "rating": 0
  }
}
```

### 4. Test Authenticated Endpoint
```bash
curl -X GET http://localhost:8080/api/complaints \
  -u user:password123
```

**Expected Response:**
```json
{
  "status": 404,
  "message": "No complaints found matching the criteria",
  "data": null
}
```

---

## 🛡️ Security Features Enabled

✅ **CSRF Protection**: Disabled for API (as APIs are typically stateless)  
✅ **HTTP Basic Authentication**: Enabled for easy API testing  
✅ **Form Login**: Enabled for web-based login  
✅ **Role-Based Access Control**: ADMIN role for sensitive operations  
✅ **Password Encryption**: BCrypt hashing  

---

## 🔑 User Roles

### USER Role
Can access:
- Raise complaints
- View complaints
- Submit feedback
- Request documents

### ADMIN Role
Can access everything USER can, plus:
- Complete/resolve complaints
- Delete complaints
- Issue documents

---

## ⚠️ Important Notes

1. **Default Password**: The BCrypt hash in the examples is for `password123`
   - Hash: `$2a$10$rBV2/L8T5.KxTgXEbfKF7eWNbVPLdNVfQQp8kKCWdZ3Fn2qPdq0Cy`

2. **Role Format**: In database, store as `USER` or `ADMIN`
   - Spring Security automatically adds `ROLE_` prefix
   - Use `.hasRole("ADMIN")` in config (not `ROLE_ADMIN`)

3. **Testing with Postman**:
   - Always select "Basic Auth" in Authorization tab
   - Don't forget to enter credentials for protected endpoints

4. **Testing with Frontend**:
   - Include `Authorization: Basic <base64(username:password)>` header
   - Or use session-based authentication with login form

---

## 🐛 Troubleshooting

### Issue: Still Getting Login Page
**Cause**: Using wrong endpoint or forgot authentication  
**Solution**: Check if endpoint requires auth and provide credentials

### Issue: 403 Forbidden
**Cause**: User doesn't have required role  
**Solution**: Check user role in database, ensure ADMIN for admin endpoints

### Issue: 401 Unauthorized
**Cause**: Wrong username/password  
**Solution**: Verify credentials, check BCrypt hash matches

### Issue: Can't Create Complaint
**Cause**: Not authenticated  
**Solution**: Add `-u username:password` to cURL or Basic Auth in Postman

---

## 📚 Quick Reference

| Endpoint | Method | Auth Required | Role | Description |
|----------|--------|---------------|------|-------------|
| `/api/documents/verify` | GET | ❌ No | Public | Verify document |
| `/api/feedback/rating` | GET | ❌ No | Public | Overall rating |
| `/api/complaints/raise` | POST | ✅ Yes | USER | Raise complaint |
| `/api/complaints` | GET | ✅ Yes | USER | Get complaints |
| `/api/feedback` | POST | ✅ Yes | USER | Submit feedback |
| `/api/documents/request` | POST | ✅ Yes | USER | Request document |
| `/api/complaints/{id}/complete` | PUT | ✅ Yes | ADMIN | Complete complaint |
| `/api/complaints/{id}` | DELETE | ✅ Yes | ADMIN | Delete complaint |
| `/api/documents/issue/{id}` | POST | ✅ Yes | ADMIN | Issue document |

---

## ✅ Updated Files

- ✅ `Config/WebSecurityConfig.java` - Security configuration with proper endpoint rules
- ✅ Enabled HTTP Basic Auth for API testing
- ✅ Role-based access control configured

---

**Last Updated**: December 30, 2025  
**Status**: ✅ **READY FOR TESTING WITH AUTHENTICATION**


