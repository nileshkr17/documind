
# Big picture / MVP


```
# MVP goal: 
Upload document(s) → ingest (chunk + embed) → store vectors → ask question → return an answer strictly grounded in retrieved chunks + show citations (file + page/section).
Deliverable you can demo: a single-page UI showing upload, a chat box, answer with citations, and a downloadable README + Docker Compose to run locally.
```


## How to Run the Backend (Before React Frontend)

### 1. Start the Python Embedding Service

```
cd embedding-service
# (Optional) Create and activate a virtual environment:
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn app:app --reload
```
The service runs at http://localhost:8000

### 2. Start PostgreSQL Database
- Make sure PostgreSQL is running and accessible.
- Ensure the database and tables are created as per your Spring Boot configuration (`application.properties`).

### 3. Start the Java Spring Boot Backend

```
cd api
./mvnw clean install
./mvnw spring-boot:run
```
The backend runs at http://localhost:8080

### 4. Test the Endpoints
- Use Postman or curl to test:
	- Document upload: `POST /api/upload`
	- Search: `POST /api/search` with JSON body like `{ "question": "cancel an order" }`

### 5. (Optional) Upload Documents
- Use the upload endpoint or UI to upload documents for chunking and embedding.

Once these are running, you can start the React frontend (see web-react/ for instructions).


- Created modular monorepo for RAG QA (API, RAG worker, frontend, infra)
