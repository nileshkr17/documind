##Documind

A microservices-based RAG (Retrieval-Augmented Generation) application with JWT authentication for intelligent document processing and Q&A.

📚 **[Quick Start Guide](./QUICKSTART.md)** | 📖 **[API Documentation](./API_DOCS.md)**

<img width="1728" height="963" alt="Screenshot 2025-08-29 at 3 00 46 AM" src="https://github.com/user-attachments/assets/016e4953-e984-44d6-a2d3-5fd1f66a42b2" />


<img width="1728" height="957" alt="Screenshot 2025-08-29 at 3 03 17 AM" src="https://github.com/user-attachments/assets/ecbb297a-13ad-4a18-a215-b345c12c1e4d" />


<img width="1727" height="962" alt="Screenshot 2025-08-29 at 3 03 28 AM" src="https://github.com/user-attachments/assets/1fdf1cd2-294d-4724-afa5-655b1fdf01ad" />

## Database (PostgreSQL) Commands
### 1. Open PostgreSQL Shell

```
psql -U <username> -d <database>
```
Replace `<username>` and `<database>` with your actual PostgreSQL username and database name (see `application.properties`).

### 2. List All Tables

```
\dt
```

### 3. Show Table Schema

```
\d <table_name>
```

### 4. List All Commands (Assuming a 'command' or 'commands' table)

```
SELECT * FROM command;
-- or
SELECT * FROM commands;
```

### 5. List All Chunks

```
SELECT * FROM document_chunk;
```

### 6. Clear (Delete) All Chunks

```
DELETE FROM document_chunk;
```

### 7. Exit psql

```
\q
```

# Big picture / MVP


```
# MVP goal: 
Upload document(s) → ingest (chunk + embed) → store vectors → ask question → return an answer strictly grounded in retrieved chunks + show citations (file + page/section).
Deliverable you can demo: a single-page UI showing upload, a chat box, answer with citations, and a downloadable README + Docker Compose to run locally.
```


## How to Run the Backend (Before React Frontend)

### 1. Start the Authentication Service

```
cd auth-service
mvn clean install
mvn spring-boot:run
```
The auth service runs at http://localhost:8082

### 2. Start the Python Embedding Service

```
cd embedding-service
# (Optional) Create and activate a virtual environment:
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn app:app --reload
```
The service runs at http://localhost:8000

### 3. Start PostgreSQL Database
- Make sure PostgreSQL is running and accessible.
- Ensure the database and tables are created as per your Spring Boot configuration (`application.properties`).

### 4. Start the Java Spring Boot Backend

```
cd api
./mvnw clean install
./mvnw spring-boot:run
```
The backend runs at http://localhost:8080


### 5. Test the Endpoints
- Use Postman or curl to test:
	- Document upload: `POST /api/upload`
	- Search: `POST /api/search` with JSON body like `{ "question": "cancel an order" }`

### 6. Delete All Chunks (Optional)
- To delete all document chunks from the database, use:

```
curl -X DELETE http://localhost:8080/api/chunks
```
Or use Postman to send a DELETE request to `/api/chunks`.


### 7. (Optional) Upload Documents
- Use the upload endpoint or UI to upload documents for chunking and embedding.

Once these are running, you can start the React frontend (see web-react/ for instructions).

## Microservices Architecture

Documind now includes multiple microservices:

1. **Auth Service (Port 8082)** - JWT-based authentication and user management
   - User registration and login
   - JWT token generation and validation
   - Role-based access control
   - See `auth-service/README.md` for details

2. **Main API (Port 8080)** - Document processing and RAG operations
   - Document upload and management
   - Chunking and processing
   - Search and query endpoints

3. **Embedding Service (Port 8000)** - Python-based embedding generation
   - Sentence transformer embeddings
   - Vector storage in PostgreSQL
   - LLM integration for response generation

All services share the same PostgreSQL database running on port 5432.

## Running with Docker Compose

The easiest way to run all services is using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Auth Service on port 8082
- Main API on port 8080
- Embedding Service on port 8000

To stop all services:
```bash
docker-compose down
```

To view logs:
```bash
docker-compose logs -f
```

## Testing the Auth Service

Use the provided test script:
```bash
./test-auth.sh
```

Or test manually with curl:

**Register a new user:**
```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securePass123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePass123"
  }'
```

The response will include a JWT token that should be used in the `Authorization: Bearer <token>` header for authenticated requests to the main API.

- Created modular monorepo for RAG QA (API, RAG worker, frontend, infra)
