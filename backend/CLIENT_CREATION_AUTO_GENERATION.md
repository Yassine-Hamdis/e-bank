# Client Creation with Auto-Generated Fields

## Overview

The client creation endpoint has been updated to automatically generate `clientId`, `identificationNumber`, and `enrollmentDate` fields, while making `username`, `email`, and `phoneNumber` required fields.

## Changes Made

### 1. Auto-Generated Fields

- **clientId**: Automatically generated with format `CLT######` (e.g., `CLT001001`)
- **identificationNumber**: Automatically generated with format `ID{YEAR}########` (e.g., `ID2024100001`)
- **enrollmentDate**: Automatically set to current date/time

### 2. Required Fields

The following fields are now required in the request:
- **username**: Must be unique, 3-50 characters
- **email**: Must be valid email format and unique
- **phoneNumber**: Required field, max 20 characters
- **address**: Required field, max 500 characters

### 3. Optional Fields

- **nationalId**: Optional field

## API Endpoint

**POST** `/api/agent/clients`

### Request Body Example

```json
{
  "address": "123 Main Street, Casablanca, Morocco",
  "username": "john_doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+212612345678",
  "nationalId": "BE123456"
}
```

### Response Example

```json
{
  "client": {
    "id": 1,
    "clientId": "CLT001001",
    "identificationNumber": "ID2024100001",
    "enrollmentDate": "2024-06-02T10:30:00Z",
    "address": "123 Main Street, Casablanca, Morocco",
    "username": "john_doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+212612345678",
    "nationalId": "BE123456",
    "status": "ACTIVE"
  },
  "account": {
    "id": 1,
    "accountId": "ACC123456",
    "clientId": "CLT001001",
    "balance": 0.00,
    "accountType": "CHECKING",
    "status": "ACTIVE",
    "currency": "MAD"
  },
  "cryptoWallet": {
    "id": 1,
    "walletAddress": "1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t",
    "clientId": "CLT001001",
    "status": "ACTIVE"
  }
}
```

## Validation Rules

### Username
- Required
- Must be unique across all users
- 3-50 characters
- Cannot be empty or whitespace only

### Email
- Required
- Must be valid email format
- Must be unique across all users
- Max 100 characters

### Phone Number
- Required
- Max 20 characters
- Cannot be empty or whitespace only

### Address
- Required
- Max 500 characters
- Cannot be empty or whitespace only

### National ID
- Optional
- Max 20 characters

## Error Responses

### Validation Errors

```json
{
  "error": "Validation failed",
  "message": "Username is required",
  "timestamp": 1703123456789
}
```

### Duplicate Username

```json
{
  "error": "Failed to create client",
  "message": "Username already exists: john_doe",
  "timestamp": 1703123456789
}
```

### Duplicate Email

```json
{
  "error": "Failed to create client",
  "message": "Email already exists: john.doe@example.com",
  "timestamp": 1703123456789
}
```

## Implementation Details

### ID Generation

- **ClientId**: Uses `IdGeneratorUtil.generateClientId()` with format `CLT######`
- **IdentificationNumber**: Uses `IdGeneratorUtil.generateIdentificationNumber()` with format `ID{YEAR}########`
- **Uniqueness**: Both IDs are checked for uniqueness and regenerated if duplicates exist

### Automatic Account and Wallet Creation

When a client is created:
1. A checking account is automatically created with zero balance
2. A crypto wallet is automatically created with active status
3. Credentials are sent via email to the client

### Password Generation

- A secure password is automatically generated for the client
- The password is sent via email along with login credentials
- The password is encrypted before storing in the database

## Testing

The implementation includes comprehensive unit tests covering:
- Auto-generation of client ID and identification number
- Required field validation
- Duplicate username/email validation
- Unique ID generation with collision handling

Run tests with:
```bash
mvn test -Dtest=BankAgentServiceClientCreationTest
```

## Files Modified

1. `src/main/java/com/ebanking/dto/request/ClientCreateRequest.java`
2. `src/main/java/com/ebanking/service/impl/BankAgentServiceImpl.java`
3. `src/main/java/com/ebanking/controller/BankAgentController.java`
4. `src/main/java/com/ebanking/util/IdGeneratorUtil.java` (new file)

## Backward Compatibility

This change is **not backward compatible** with existing client creation requests that include `clientId`, `identificationNumber`, or `enrollmentDate` fields. These fields will now be ignored and auto-generated instead.

Existing API consumers should update their requests to only include the required fields: `username`, `email`, `phoneNumber`, and `address`.
