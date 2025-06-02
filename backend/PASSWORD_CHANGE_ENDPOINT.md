# Password Change Endpoint Documentation

## Overview
This document describes the new password change endpoint that allows all authenticated users (ADMIN, AGENT, CLIENT) to change their passwords.

## Endpoint Details

### Change Password
**URL:** `PUT /api/user/change-password`

**Authentication:** Required (JWT Token)

**Authorization:** All authenticated users (ROLE_ADMIN, ROLE_AGENT, ROLE_CLIENT)

**Content-Type:** `application/json`

### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Request Body
```json
{
    "currentPassword": "string",
    "newPassword": "string", 
    "confirmPassword": "string"
}
```

#### Field Validations
- `currentPassword`: Required, not blank
- `newPassword`: Required, minimum 8 characters
- `confirmPassword`: Required, must match newPassword

### Response

#### Success Response (200 OK)
```json
{
    "success": true,
    "message": "Password changed successfully",
    "username": "user123",
    "timestamp": "2024-01-01T12:00:00Z"
}
```

#### Error Responses

**400 Bad Request - Password Mismatch**
```json
{
    "success": false,
    "message": "New password and confirm password do not match",
    "username": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

**400 Bad Request - Wrong Current Password**
```json
{
    "success": false,
    "message": "Current password is incorrect",
    "username": "user123",
    "timestamp": "2024-01-01T12:00:00Z"
}
```

**400 Bad Request - Same Password**
```json
{
    "success": false,
    "message": "New password must be different from current password",
    "username": "user123",
    "timestamp": "2024-01-01T12:00:00Z"
}
```

**401 Unauthorized - Invalid Token**
```json
{
    "error": "Unable to authenticate user",
    "message": "Invalid token",
    "timestamp": 1704110400000
}
```

## Usage Examples

### cURL Example
```bash
curl -X PUT http://localhost:8081/api/user/change-password \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldPassword123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

### JavaScript/Fetch Example
```javascript
const changePassword = async (currentPassword, newPassword, confirmPassword) => {
    const response = await fetch('/api/user/change-password', {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            currentPassword,
            newPassword,
            confirmPassword
        })
    });
    
    const result = await response.json();
    
    if (result.success) {
        console.log('Password changed successfully');
    } else {
        console.error('Password change failed:', result.message);
    }
    
    return result;
};
```

## Security Features

1. **Authentication Required**: Only authenticated users can access this endpoint
2. **User Isolation**: Users can only change their own password (extracted from JWT token)
3. **Current Password Verification**: Must provide correct current password
4. **Password Validation**: New password must meet minimum requirements (8+ characters)
5. **Password Encryption**: Passwords are encrypted using BCrypt
6. **Different Password Requirement**: New password must be different from current password

## Additional Endpoint

### Get Current User Profile
**URL:** `GET /api/user/profile`

**Authentication:** Required (JWT Token)

**Response:**
```json
{
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "role": "ROLE_CLIENT",
    "status": "ACTIVE"
}
```

## Implementation Details

### Files Added/Modified

1. **New DTOs:**
   - `PasswordChangeRequest.java` - Request DTO for password change
   - `PasswordChangeResponse.java` - Response DTO for password change

2. **New Service Layer:**
   - `UserService.java` - Interface for user operations
   - `UserServiceImpl.java` - Implementation of user operations

3. **New Controller:**
   - `UserController.java` - REST controller for user operations

4. **Modified Files:**
   - `UserDetailsServiceImpl.java` - Added getStatus() method to UserPrincipal

### Database Impact
- No database schema changes required
- Uses existing `users` table and password field
- Updates `updated_at` timestamp when password is changed

## Testing
The implementation includes comprehensive unit tests:
- `UserServiceTest.java` - Tests for all password change scenarios
- `UserControllerTest.java` - Tests for controller endpoints

Run tests with:
```bash
mvn test -Dtest=UserServiceTest
```

## Notes
- The endpoint works for all user roles (ADMIN, AGENT, CLIENT)
- Password changes are logged for security auditing
- The implementation follows Spring Security best practices
- JWT token validation ensures only authenticated users can change passwords
