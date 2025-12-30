# Smart Municipal Services - API Documentation

This document provides detailed information about all available REST API endpoints, including request formats, parameters, and sample requests.

---

## Table of Contents

1. [Complaint Controller](#1-complaint-controller)
2. [Feedback Controller](#2-feedback-controller)
3. [Document Controller](#3-document-controller)

---

## 1. Complaint Controller

Base URL: `/api/complaints`

### 1.1 Raise a Complaint

**Endpoint:** `POST /api/complaints/raise`

**Description:** Create a new complaint with an image attachment.

**Request Type:** `multipart/form-data`

**Request Parts:**
- `complaint` (JSON) - Complaint details
- `image` (File) - Image file

**Request Body Structure:**
```json
{
  "complaintType": "ROAD",
  "complaintTitle": "Pothole on Main Street",
  "message": "There is a large pothole near the intersection causing traffic issues"
}
```

**Valid Complaint Types:**
- `ROAD`
- `WATER`
- `ELECTRICITY`
- `SANITATION`
- `STREETLIGHT`
- `DRAINAGE`
- `OTHER`

**Sample cURL Request:**
```bash
curl -X POST http://localhost:8080/api/complaints/raise \
  -F "complaint={\"complaintType\":\"ROAD\",\"complaintTitle\":\"Pothole on Main Street\",\"message\":\"Large pothole causing traffic issues\"};type=application/json" \
  -F "image=@/path/to/image.jpg"
```

**Sample Response:**
```text
Complaint raised successfully with ID: 123
```

---

### 1.2 Get Complaints (with Filters)

**Endpoint:** `GET /api/complaints`

**Description:** Retrieve complaints with optional filtering by status, type, and date.

**Query Parameters:**
- `status` (optional) - Filter by complaint status (GENERATED, IN_PROGRESS, COMPLETED)
- `type` (optional) - Filter by complaint type (ROAD, WATER, etc.)
- `date` (optional) - Filter by date (format: yyyy-MM-dd)

**Sample Requests:**

**Get all complaints:**
```bash
curl -X GET http://localhost:8080/api/complaints
```

**Filter by status:**
```bash
curl -X GET "http://localhost:8080/api/complaints?status=GENERATED"
```

**Filter by type:**
```bash
curl -X GET "http://localhost:8080/api/complaints?type=ROAD"
```

**Filter by date:**
```bash
curl -X GET "http://localhost:8080/api/complaints?date=2025-12-30"
```

**Multiple filters:**
```bash
curl -X GET "http://localhost:8080/api/complaints?status=GENERATED&type=ROAD&date=2025-12-30"
```

**Sample Response:**
```json
{
  "id": 123,
  "complaintType": "ROAD",
  "complaintTitle": "Pothole on Main Street",
  "message": "Large pothole causing traffic issues",
  "generatedAt": "2025-12-30T10:30:00",
  "complaintStatus": "GENERATED"
}
```

**Response when no complaints found:**
- HTTP Status: `204 No Content`

---

### 1.3 Update Complaint Status (Admin)

**Endpoint:** `PUT /api/complaints/{complaintId}/complete`

**Description:** Mark a complaint as COMPLETED (Admin only).

**Path Variables:**
- `complaintId` (String) - ID of the complaint to update

**Sample Request:**
```bash
curl -X PUT http://localhost:8080/api/complaints/123/complete
```

**Sample Response:**
```text
Complaint with ID 123 updated to RESOLVED.
```

**Error Response:**
```text
Complaint with ID 123 not found.
```

---

### 1.4 Delete Complaint (Admin)

**Endpoint:** `DELETE /api/complaints/{complaintId}`

**Description:** Delete a complaint from the system (Admin only).

**Path Variables:**
- `complaintId` (String) - ID of the complaint to delete

**Sample Request:**
```bash
curl -X DELETE http://localhost:8080/api/complaints/123
```

**Sample Response:**
```text
Complaint with ID 123 deleted.
```

**Error Response:**
```text
Complaint with ID 123 not found.
```

---

## 2. Feedback Controller

Base URL: `/api/feedback`

### 2.1 Create Feedback

**Endpoint:** `POST /api/feedback`

**Description:** Submit feedback for a completed complaint.

**Request Type:** `application/json`

**Request Body:**
```json
{
  "complaintId": 123,
  "description": "The road repair was done quickly and professionally. Great work!",
  "rating": 5
}
```

**Request Body Fields:**
- `complaintId` (int, required) - ID of the complaint to provide feedback for
- `description` (String, optional) - Feedback description
- `rating` (long, required) - Rating from 1 to 5

**Rating Scale:**
- `1` - Very Poor
- `2` - Poor
- `3` - Average
- `4` - Good
- `5` - Excellent

**Sample cURL Request:**
```bash
curl -X POST http://localhost:8080/api/feedback \
  -H "Content-Type: application/json" \
  -d '{
    "complaintId": 123,
    "description": "The road repair was done quickly and professionally. Great work!",
    "rating": 5
  }'
```

**Sample Response:**
```text
Feedback created successfully for complaint ID: 123
```

**Error Responses:**
```text
Invalid rating. Rating must be between 1 and 5
```
```text
Complaint not found with id: 123
```

---

### 2.2 Get Overall Rating

**Endpoint:** `GET /api/feedback/rating`

**Description:** Get the overall rating statistics for all completed tasks (Public endpoint).

**Sample Request:**
```bash
curl -X GET http://localhost:8080/api/feedback/rating
```

**Sample Response:**
```json
{
  "complaintId": 0,
  "rating": 4
}
```

**Note:** The `rating` field contains the rounded average rating of all feedbacks. The `complaintId` field is not used in this response (set to 0).

---

## 3. Document Controller

Base URL: `/api/documents`

### 3.1 Request Document

**Endpoint:** `POST /api/documents/request`

**Description:** Submit a new document request/application.

**Request Type:** `application/json`

**Request Body:**
```json
{
  "category": "BIRTH_CERTIFICATE",
  "applicantName": "John Doe",
  "applicantEmail": "john.doe@example.com",
  "applicantPhone": "1234567890",
  "additionalDetails": "Required for school admission"
}
```

**Valid Document Categories:**
- `BIRTH_CERTIFICATE`
- `DEATH_CERTIFICATE`
- `MARRIAGE_CERTIFICATE`
- `PROPERTY_TAX_CERTIFICATE`
- `TRADE_LICENSE`
- `BUILDING_PERMIT`
- `NOC` (No Objection Certificate)
- `OTHER`

**Request Body Fields:**
- `category` (String, required) - Type of document requested
- `applicantName` (String, required) - Name of the applicant
- `applicantEmail` (String, required) - Email address
- `applicantPhone` (String, required) - Phone number
- `additionalDetails` (String, optional) - Any additional information

**Sample cURL Request:**
```bash
curl -X POST http://localhost:8080/api/documents/request \
  -H "Content-Type: application/json" \
  -d '{
    "category": "BIRTH_CERTIFICATE",
    "applicantName": "John Doe",
    "applicantEmail": "john.doe@example.com",
    "applicantPhone": "1234567890",
    "additionalDetails": "Required for school admission"
  }'
```

**Sample Response:**
```text
Document request submitted successfully with ID: 456
```

---

### 3.2 Verify Document (QR Code Scan)

**Endpoint:** `GET /api/documents/verify`

**Description:** Verify the authenticity of a document by scanning its QR code (Public endpoint).

**Query Parameters:**
- `docId` (Integer, required) - Document ID
- `hash` (String, required) - Security hash from the QR code

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/documents/verify?docId=456&hash=abc123def456xyz789"
```

**Sample Response (Authentic):**
```json
{
  "status": 200,
  "message": "Document verified successfully",
  "data": {
    "isAuthentic": true,
    "message": "Document is authentic and verified.",
    "documentId": 456
  }
}
```

**Sample Response (Not Authentic):**
```json
{
  "status": 200,
  "message": "Document verification failed",
  "data": {
    "isAuthentic": false,
    "message": "Document verification failed. This document may be fraudulent.",
    "documentId": 456
  }
}
```

---

### 3.3 Issue Document (Admin)

**Endpoint:** `POST /api/documents/issue/{applicationId}`

**Description:** Verify an application and issue the requested document (Admin only).

**Path Variables:**
- `applicationId` (Integer) - ID of the application to process

**Sample Request:**
```bash
curl -X POST http://localhost:8080/api/documents/issue/456
```

**Sample Response:**
```json
{
  "status": 200,
  "message": "Document issued successfully for application ID: 456",
  "data": null
}
```

**Error Response:**
```json
{
  "status": 200,
  "message": "Application with ID 456 not found.",
  "data": null
}
```

---

## Response Structure

All API endpoints now return a standardized JSON response with the following structure:

```json
{
  "status": 200,
  "message": "Success or error message from service layer",
  "data": "Response data (can be object, array, or null)"
}
```

**Fields:**
- `status` (int) - HTTP status code (200, 400, 404, 500, etc.)
- `message` (String) - Descriptive message about the operation result
- `data` (Generic) - Response payload (can be any type or null)

---

## Common HTTP Status Codes

- `200 OK` - Request successful
- `204 No Content` - Request successful but no data to return
- `400 Bad Request` - Invalid request format or parameters
- `404 Not Found` - Requested resource not found
- `500 Internal Server Error` - Server error

---

## Testing with Postman

### Importing Requests

You can import these sample requests into Postman:

1. **Create Complaint:**
   - Method: POST
   - URL: `http://localhost:8080/api/complaints/raise`
   - Body: form-data
     - Key: `complaint` (JSON)
     - Value: `{"complaintType":"ROAD","complaintTitle":"Test","message":"Test message"}`
     - Key: `image` (File)
     - Value: Select an image file

2. **Get Complaints:**
   - Method: GET
   - URL: `http://localhost:8080/api/complaints?status=GENERATED`

3. **Create Feedback:**
   - Method: POST
   - URL: `http://localhost:8080/api/feedback`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON): `{"complaintId":1,"description":"Good work","rating":5}`

4. **Request Document:**
   - Method: POST
   - URL: `http://localhost:8080/api/documents/request`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON): Document request object

---

## Notes

- Replace `localhost:8080` with your actual server address and port
- Admin endpoints may require authentication (check security configuration)
- All timestamps are in ISO-8601 format
- File uploads support common image formats (JPG, PNG, etc.)
- Date parameters use the format `yyyy-MM-dd`

---

## Support

For additional help or to report issues, please contact the development team.

Last Updated: December 30, 2025

