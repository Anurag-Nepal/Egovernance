# Developer Quick Reference - JSON Response Pattern

## How to Use the Structured JSON Response System

### For Controllers (You're Already Done! ✅)

All controllers now automatically return structured JSON responses:

```java
@PostMapping("/endpoint")
public ResponseEntity<ApiResponse<YourDataType>> yourMethod() {
    YourDataType data = yourService.doSomething();
    return ResponseEntity.ok(ApiResponse.success("Operation successful", data));
}
```

---

### For Service Layer (How to Throw Exceptions)

Instead of returning error messages as strings, **throw exceptions**:

#### Before (❌ Old Way):
```java
public String updateComplaint(String id) {
    if (!complaintRepository.existsById(id)) {
        return "Complaint not found";  // BAD - returns error as string
    }
    // ... update logic
    return "Complaint updated successfully";
}
```

#### After (✅ New Way):
```java
public String updateComplaint(String id) {
    if (!complaintRepository.existsById(id)) {
        throw new ResourceNotFoundException("Complaint with ID " + id + " not found");
    }
    // ... update logic
    return "Complaint with ID " + id + " updated to RESOLVED.";
}
```

---

## Available Exceptions

### 1. ResourceNotFoundException (404)
Use when a resource is not found:
```java
throw new ResourceNotFoundException("User with ID " + id + " not found");
```
**Returns:**
```json
{
  "status": 404,
  "message": "User with ID 123 not found",
  "data": null
}
```

### 2. BadRequestException (400)
Use for invalid input or validation errors:
```java
throw new BadRequestException("Invalid email format");
```
**Returns:**
```json
{
  "status": 400,
  "message": "Invalid email format",
  "data": null
}
```

### 3. IllegalArgumentException (400)
Already built-in, automatically handled:
```java
throw new IllegalArgumentException("Rating must be between 1 and 5");
```
**Returns:**
```json
{
  "status": 400,
  "message": "Rating must be between 1 and 5",
  "data": null
}
```

### 4. Generic Exception (500)
Any unhandled exception automatically becomes:
```json
{
  "status": 500,
  "message": "An unexpected error occurred: ...",
  "data": null
}
```

---

## ApiResponse Factory Methods

### Success with data:
```java
ApiResponse.success("Message here", yourDataObject)
```

### Success without data:
```java
ApiResponse.success("Message here")
```

### Manual error (if needed):
```java
ApiResponse.error(404, "Custom error message")
ApiResponse.error("Error message")  // defaults to 400
```

---

## Examples by Use Case

### Example 1: Create Operation
```java
@PostMapping
public ResponseEntity<ApiResponse<Void>> create(@RequestBody Request req) {
    String message = service.create(req);
    return ResponseEntity.ok(ApiResponse.success(message));
}
```

### Example 2: Get Operation (Single Item)
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable Integer id) {
    UserDTO user = service.findById(id);
    return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
}
```

### Example 3: Get Operation (List)
```java
@GetMapping
public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
    List<UserDTO> users = service.findAll();
    return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
}
```

### Example 4: Update Operation
```java
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> update(@PathVariable Integer id) {
    String message = service.update(id);
    return ResponseEntity.ok(ApiResponse.success(message));
}
```

### Example 5: Delete Operation
```java
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
    String message = service.delete(id);
    return ResponseEntity.ok(ApiResponse.success(message));
}
```

---

## Service Layer Best Practices

### ✅ DO:
```java
// Throw exceptions for errors
if (user == null) {
    throw new ResourceNotFoundException("User not found");
}

// Return success messages as strings
return "User created successfully with ID: " + user.getId();
```

### ❌ DON'T:
```java
// Don't return error indicators
if (user == null) {
    return null;  // BAD
    return "";    // BAD
    return "Error: User not found";  // BAD - use exception instead
}
```

---

## Testing Your API

### With cURL:
```bash
curl -X POST http://localhost:8080/api/complaints/raise \
  -F "complaint={\"complaintType\":\"ROAD\",\"complaintTitle\":\"Test\",\"message\":\"Test\"};type=application/json" \
  -F "image=@image.jpg" | json_pp
```

### Expected Response:
```json
{
  "status": 200,
  "message": "Complaint raised successfully",
  "data": "Complaint raised successfully with ID: 123"
}
```

---

## Frontend Integration

### JavaScript/TypeScript Example:
```javascript
async function createComplaint(data) {
  const response = await fetch('/api/complaints/raise', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  
  if (result.status === 200) {
    console.log('Success:', result.message);
    console.log('Data:', result.data);
  } else {
    console.error('Error:', result.message);
  }
}
```

### React Example:
```jsx
const response = await api.post('/complaints/raise', formData);
if (response.data.status === 200) {
  toast.success(response.data.message);
  // Use response.data.data if needed
} else {
  toast.error(response.data.message);
}
```

---

## Summary

🎯 **Controllers** → Return `ApiResponse<T>` wrapped in `ResponseEntity`  
🎯 **Services** → Throw exceptions for errors, return strings for success  
🎯 **GlobalExceptionHandler** → Automatically converts exceptions to JSON  
🎯 **Frontend** → Always gets consistent `{status, message, data}` structure  

---

**Remember:** The system is controller-agnostic. All new controllers automatically get this behavior!


