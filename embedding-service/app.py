import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import asyncpg
from sentence_transformers import SentenceTransformer
import numpy as np

# Load environment variables for DB connection
DB_HOST = os.getenv("DB_HOST", "127.0.0.1")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "ragbot")
DB_USER = os.getenv("DB_USER", "raguser")
DB_PASS = os.getenv("DB_PASS", "ragpass")


DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Or restrict to ["http://127.0.0.1:5500"]
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

class EmbedRequest(BaseModel):
    document_id: str
    chunks: List[str]

class EmbedResponse(BaseModel):
    embeddings: List[List[float]]

class SearchRequest(BaseModel):
    query: str
    top_k: int = 5

class SearchResult(BaseModel):
    chunk_text: str
    score: float

@app.on_event("startup")
async def startup():
    app.state.pool = await asyncpg.create_pool(DATABASE_URL, min_size=1, max_size=10)

@app.on_event("shutdown")
async def shutdown():
    await app.state.pool.close()

@app.post("/embed", response_model=EmbedResponse)
async def embed_chunks(req: EmbedRequest):
    if not req.chunks:
        raise HTTPException(status_code=400, detail="No chunks provided")
    print(f"[EMBED] Received document_id: {req.document_id}")
    print(f"[EMBED] Received {len(req.chunks)} chunks. First chunk: {req.chunks[0] if req.chunks else 'N/A'}")
    try:
        embeddings = model.encode(req.chunks, convert_to_numpy=True).tolist()
        def to_pgvector_str(emb):
            return '[' + ','.join(f'{x:.6f}' for x in emb) + ']'
        async with app.state.pool.acquire() as conn:
            await conn.executemany(
                "INSERT INTO document_chunks (document_id, chunk_text, embedding) VALUES ($1, $2, $3)",
                [
                    (req.document_id, chunk, to_pgvector_str(emb))
                    for chunk, emb in zip(req.chunks, embeddings)
                ]
            )
        return {"embeddings": embeddings}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/search", response_model=List[SearchResult])
async def search_chunks(req: SearchRequest):
    try:
        query_emb = model.encode([req.query], convert_to_numpy=True)[0].tolist()
        def to_pgvector_str(emb):
            return '[' + ','.join(f'{x:.6f}' for x in emb) + ']'
        query_emb_str = to_pgvector_str(query_emb)
        async with app.state.pool.acquire() as conn:
            rows = await conn.fetch(
                """
                SELECT chunk_text, embedding <#> $1::vector AS score
                FROM document_chunks
                ORDER BY embedding <#> $1::vector
                LIMIT $2
                """,
                query_emb_str, req.top_k
            )
        return [{"chunk_text": row["chunk_text"], "score": row["score"]} for row in rows]
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
