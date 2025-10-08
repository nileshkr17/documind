# Documind Quick Start Guide

Get up and running with Documind in 5 minutes!

## Prerequisites

- Docker and Docker Compose
- (Optional) Java 17+ and Maven for local development
- (Optional) Python 3.11+ for local development

## Quick Start with Docker

### 1. Clone the Repository

```bash
git clone https://github.com/nileshkr17/documind.git
cd documind
```

### 2. Start All Services

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Auth Service on port 8082
- Main API on port 8080
- Embedding Service on port 8000

### 3. Wait for Services to Start

```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

Wait until all services show as "healthy" or "running".

### 4. Test Authentication

```bash
# Register a new user
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123"
  }'
```

You'll receive a response with a JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"]
}
```

Save the token for the next steps!

### 5. Upload a Document

```bash
# Create a test document
echo "This is a test document about refund policies. You can request a refund within 30 days." > test.txt

# Upload it (replace <YOUR_TOKEN> with the token from step 4)
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -F "file=@test.txt"
```

### 6. Query Your Documents

```bash
# Search for information
curl -X POST http://localhost:8000/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is the refund policy?",
    "top_k": 3
  }'
```

## Alternative: Manual Start (Without Docker)

### 1. Start PostgreSQL

```bash
# Make sure PostgreSQL is running
psql -U postgres

# Create database and user
CREATE DATABASE ragbot;
CREATE USER raguser WITH PASSWORD 'ragpass';
GRANT ALL PRIVILEGES ON DATABASE ragbot TO raguser;
```

### 2. Start Auth Service

```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

### 3. Start Embedding Service

```bash
cd embedding-service
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn app:app --reload
```

### 4. Start Main API

```bash
cd api
mvn clean install
mvn spring-boot:run
```

## Testing

### Automated Test

```bash
chmod +x test-auth.sh
./test-auth.sh
```

### Manual Testing

See the complete API documentation in [API_DOCS.md](./API_DOCS.md)

## Common Issues

### Port Already in Use

If you see "port already in use" errors:

```bash
# Check what's using the port
lsof -i :8080  # or :8082, :8000, :5432

# Stop the service using that port, then restart docker-compose
docker-compose down
docker-compose up -d
```

### Database Connection Failed

```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check PostgreSQL logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres
```

### Token Invalid/Expired

Tokens expire after 24 hours. Simply login again to get a new token:

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "password123"
  }'
```

## Next Steps

1. **Read the API Docs**: Check [API_DOCS.md](./API_DOCS.md) for complete API reference
2. **Upload Real Documents**: Try uploading PDF or DOCX files
3. **Explore the Code**: Check out the auth service in `/auth-service`
4. **Customize**: Modify JWT settings, add roles, or extend the API

## Architecture Overview

```
┌──────────────────┐
│   Web Client     │
└────────┬─────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌─────────┐ ┌────────┐     ┌──────────────┐
│  Auth   │ │  Main  │────>│  Embedding   │
│ Service │ │  API   │     │   Service    │
└────┬────┘ └───┬────┘     └──────┬───────┘
     │          │                  │
     └──────────┴──────────────────┘
                │
          ┌─────▼─────┐
          │ PostgreSQL│
          └───────────┘
```

## Support

- **Issues**: https://github.com/nileshkr17/documind/issues
- **Documentation**: See README.md and API_DOCS.md
- **Examples**: Check the test-auth.sh script

Happy coding! 🚀
