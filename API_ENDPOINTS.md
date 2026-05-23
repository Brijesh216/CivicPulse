# CivicPulse API Endpoints Documentation

**Base URL:** `http://localhost:8081`  
**Status:** ❌ Currently Not Running (Requires fix to SecurityConfig.java)

---

## Authentication Endpoints

### 1. User Registration
**Endpoint:** `POST /api/auth/register`  
**Authentication:** Not required  
**CORS:** Enabled  

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword@123",
  "role": "ROLE_CITIZEN",
  "departmentId": null
}
```

**Request Fields:**
- `name` (string, required): User's full name
- `email` (string, required): Valid email address
- `password` (string, required): Password for account
- `role` (string, optional): User role - `ROLE_CITIZEN` or `ROLE_OFFICER` (default: ROLE_CITIZEN)
- `departmentId` (integer, conditional): Required only if role is `ROLE_OFFICER`

**Success Response (200):**
```json
{
  "message": "User registered successfully!"
}
```

**Error Responses:**
- `400 Bad Request`: Email already exists
```json
{
  "message": "Error: Email is already in use!"
}
```
- `400 Bad Request`: Department ID missing for Officer role
```json
{
  "message": "Error: Department ID is required for Officer role."
}
```

**Example cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "password": "SecurePass@456",
    "role": "ROLE_CITIZEN"
  }'
```

---

### 2. User Login
**Endpoint:** `POST /api/auth/login`  
**Authentication:** Not required  
**CORS:** Enabled  

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword@123"
}
```

**Request Fields:**
- `email` (string, required): User's email address
- `password` (string, required): User's password

**Success Response (200):**
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjQ1MDAwMDAwfQ.signature",
  "id": 1,
  "name": "John Doe",
  "username": "john@example.com",
  "role": "ROLE_CITIZEN"
}
```

**Response Fields:**
- `jwt` (string): JWT token for authenticated requests (valid for 24 hours)
- `id` (integer): User ID
- `name` (string): User's full name
- `username` (string): Email address used as username
- `role` (string): User's role

**Error Responses:**
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: User not found

**Example cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword@123"
  }'
```

---

## Department Endpoints

### 3. Get All Departments
**Endpoint:** `GET /api/departments`  
**Authentication:** Not required  
**CORS:** Enabled  

**Success Response (200):**
```json
[
  {
    "id": 1,
    "name": "Sanitation Department",
    "description": "Handles waste management and cleanliness"
  },
  {
    "id": 2,
    "name": "Road Maintenance",
    "description": "Manages road repairs and maintenance"
  },
  {
    "id": 3,
    "name": "Water Supply",
    "description": "Manages water distribution and quality"
  }
]
```

**Response Fields (per department):**
- `id` (integer): Department ID
- `name` (string): Department name
- `description` (string): Department description

**Example cURL:**
```bash
curl -X GET http://localhost:8081/api/departments
```

---

## Complaint Endpoints (Requires Authentication)

### 4. Create Complaint
**Endpoint:** `POST /api/complaints`  
**Authentication:** Required (JWT Token)  
**CORS:** Enabled  

**Request Header:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
  "title": "Broken streetlight on Main Street",
  "description": "The streetlight at the corner of Main and 5th is broken",
  "location": "Main Street, Corner of 5th Avenue",
  "departmentId": 1,
  "category": "Infrastructure"
}
```

**Success Response (201):**
```json
{
  "id": 1,
  "title": "Broken streetlight on Main Street",
  "description": "The streetlight at the corner of Main and 5th is broken",
  "location": "Main Street, Corner of 5th Avenue",
  "status": "OPEN",
  "citizenId": 1,
  "departmentId": 1,
  "createdAt": "2025-03-26T10:30:00Z"
}
```

**Error Responses:**
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: Insufficient permissions

---

### 5. Get User Complaints
**Endpoint:** `GET /api/complaints`  
**Authentication:** Required (JWT Token)  
**CORS:** Enabled  

**Request Header:**
```
Authorization: Bearer {jwt_token}
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "title": "Broken streetlight on Main Street",
    "description": "The streetlight at the corner of Main and 5th is broken",
    "location": "Main Street, Corner of 5th Avenue",
    "status": "OPEN",
    "citizenId": 1,
    "departmentId": 1,
    "createdAt": "2025-03-26T10:30:00Z"
  }
]
```

**Example cURL:**
```bash
curl -X GET http://localhost:8081/api/complaints \
  -H "Authorization: Bearer {jwt_token}"
```

---

## Authentication Flow Example

### Complete Flow - Register, Login, and Access Protected Resource

**Step 1: Register a new user**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "AlicePass@123"
  }'

# Response:
# {"message": "User registered successfully!"}
```

**Step 2: Login to get JWT token**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "AlicePass@123"
  }'

# Response:
# {
#   "jwt": "eyJhbGciOiJIUzI1NiJ9...",
#   "id": 1,
#   "name": "Alice Johnson",
#   "username": "alice@example.com",
#   "role": "ROLE_CITIZEN"
# }
```

**Step 3: Use JWT token to access protected endpoint**
```bash
# Save token from step 2
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Get departments (no auth needed)
curl -X GET http://localhost:8081/api/departments

# Create complaint (auth required)
curl -X POST http://localhost:8081/api/complaints \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pothole on 5th Street",
    "description": "Large pothole causing accidents",
    "location": "5th Street, Block 42",
    "departmentId": 2,
    "category": "Road Maintenance"
  }'

# Get my complaints (auth required)
curl -X GET http://localhost:8081/api/complaints \
  -H "Authorization: Bearer $TOKEN"
```

---

## JWT Token Details

**JWT Configuration (from application.properties):**
- Secret: `9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9`
- Expiration: 86400000 ms (24 hours)
- Algorithm: HS256

**Token Structure:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjQ1MDAwMDAwfQ.signature
```

**Decoded Header:**
```json
{
  "alg": "HS256"
}
```

**Decoded Payload:**
```json
{
  "sub": "john@example.com",
  "iat": 1645000000
}
```

---

## Security Notes

1. **CORS Enabled:** All endpoints allow requests from any origin (origins = "*")
2. **JWT Required:** All endpoints except `/api/auth/**` and `/api/departments` require valid JWT
3. **Token Expiration:** JWT tokens expire after 24 hours
4. **Password Hashing:** Passwords are hashed using BCrypt before storage
5. **Stateless:** Application uses stateless JWT authentication (no sessions)

---

## Database Details

**Database:** H2 In-Memory Database  
**Connection String:** `jdbc:h2:mem:civicpulse`  
**H2 Console:** `http://localhost:8081/h2-console`  
**User:** sa  
**Password:** (empty)  

**Tables:**
- `user` - User accounts
- `department` - Departments
- `officer` - Officer assignments
- `complaint` - Complaint records
- `complaint_update` - Complaint status updates
- `notification` - System notifications

---

## Error Status Codes

| Code | Meaning | Common Cause |
|------|---------|--------------|
| 200 | OK | Request succeeded |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input or missing required fields |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | User lacks permissions |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server-side error |

---

## IMPORTANT STATUS

⚠️ **Application is currently non-functional due to compilation error in SecurityConfig.java**

**Fix Required:**
- Update `DaoAuthenticationProvider` instantiation in `src/main/java/com/civicpulse/security/SecurityConfig.java` (see QUICK_FIX.md)

**After fix:**
1. Run: `mvnw.cmd clean package -DskipTests`
2. Run: `mvnw.cmd spring-boot:run`
3. Test endpoints as documented above

---

**API Documentation Version:** 1.0  
**Last Updated:** March 26, 2025  
**Status:** Pending Build Fix
