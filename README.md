# De-maker-backend

## Test Credentials
| Username | Email | Password | Role |
|----------|-------|----------|------|
| admin | admin@demaker.nl | password123 | ADMIN + MONTEUR |
| monteur1 | monteur@demaker.nl | password123 | MONTEUR |

## Test Endpoints

### 1. Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "password123"
}
```

Response:
```json
{
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

### 2. Register
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "new@demaker.nl",
  "password": "password123"
}