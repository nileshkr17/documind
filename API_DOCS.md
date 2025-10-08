# Documind API Documentation

## Overview

Documind is a microservices-based RAG (Retrieval-Augmented Generation) application with JWT authentication. This document describes all available APIs.

## Architecture

- **Auth Service** (Port 8082): User authentication and JWT token management
- **Main API** (Port 8080): Document management and RAG operations
- **Embedding Service** (Port 8000): Document embedding and vector search

## Authentication

All endpoints in the Main API (except `/api/auth/**`) require JWT authentication.

### Getting a Token

1. Register or login via Auth Service
2. Extract the `token` from the response
3. Include in requests: `Authorization: Bearer <token>`

---

## Auth Service API (Port 8082)

### Register User

Creates a new user account.

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Error Response (400 Bad Request):**
```json
"Registration failed: Username already exists"
```

---

### Login

Authenticates a user and returns a JWT token.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePass123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Error Response (400 Bad Request):**
```json
"Login failed: Bad credentials"
```

---

### Validate Token

Validates a JWT token.

**Endpoint:** `GET /api/auth/validate`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```
Token is valid
```

---

## Main API (Port 8080)

### Upload Document

Uploads a document for processing and embedding.

**Endpoint:** `POST /api/documents/upload`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Request:**
- Form field: `file` (the document file)

**Supported formats:** PDF, DOCX, TXT

**Response (200 OK):**
```json
{
  "document": {
    "id": "uuid",
    "filename": "example.pdf",
    "type": "application/pdf",
    "size": 12345,
    "filePath": "/path/to/file",
    "uploadedAt": "2025-01-15T10:30:00"
  },
  "chunkCount": 42,
  "documentType": "pdf"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@document.pdf"
```

---

### Delete All Chunks

Removes all document chunks from the database.

**Endpoint:** `DELETE /api/chunks`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```
All chunks deleted
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/chunks \
  -H "Authorization: Bearer <token>"
```

---

## Embedding Service API (Port 8000)

These endpoints are typically called by the Main API, not directly by clients.

### Generate Embeddings

Generates embeddings for document chunks.

**Endpoint:** `POST /embed`

**Request Body:**
```json
{
  "document_id": "uuid",
  "chunks": ["chunk text 1", "chunk text 2", "..."]
}
```

**Response:**
```json
{
  "embeddings": [[0.1, 0.2, ...], [0.3, 0.4, ...]]
}
```

---

### Search

Performs semantic search across documents.

**Endpoint:** `POST /search`

**Request Body:**
```json
{
  "query": "What is the refund policy?",
  "top_k": 5
}
```

**Response:**
```json
{
  "results": [
    {
      "chunk_text": "Relevant text from document...",
      "score": 0.95
    }
  ]
}
```

---

### Generate Response

Generates an AI response based on context and question.

**Endpoint:** `POST /generate`

**Request Body:**
```json
{
  "context": "Retrieved document context...",
  "question": "What is the refund policy?"
}
```

**Response:**
```json
{
  "response": "Based on the provided information, the refund policy..."
}
```

---

## Error Codes

- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions
- **500 Internal Server Error**: Server error

---

## Authentication Flow

```
┌─────────┐                 ┌──────────────┐                 ┌──────────┐
│ Client  │                 │ Auth Service │                 │ Main API │
└────┬────┘                 └──────┬───────┘                 └────┬─────┘
     │                             │                              │
     │  1. POST /api/auth/login    │                              │
     ├────────────────────────────>│                              │
     │                             │                              │
     │  2. JWT Token               │                              │
     │<────────────────────────────┤                              │
     │                             │                              │
     │  3. POST /api/documents/upload                             │
     │    Authorization: Bearer <token>                           │
     ├────────────────────────────────────────────────────────────>│
     │                             │                              │
     │                             │  4. Validate Token           │
     │                             │<─────────────────────────────┤
     │                             │                              │
     │                             │  5. Token Valid              │
     │                             ├─────────────────────────────>│
     │                             │                              │
     │  6. Upload Response         │                              │
     │<────────────────────────────────────────────────────────────┤
     │                             │                              │
```

---

## Token Expiration

JWT tokens expire after 24 hours (86400000 ms). After expiration, users must login again to obtain a new token.

---

## Development Tips

1. **Testing**: Use the `test-auth.sh` script to quickly test authentication
2. **Debugging**: Check service logs with `docker-compose logs -f <service-name>`
3. **Database**: Connect to PostgreSQL with `psql -U raguser -d ragbot -h localhost`
4. **CORS**: Configured to accept requests from `http://localhost:8081` and `http://localhost:3000`

---

## Security Considerations

- JWT secret should be changed in production
- Use HTTPS in production
- Store tokens securely on the client side
- Never commit secrets to version control
- Implement rate limiting for production use
- Consider implementing refresh tokens for better security
