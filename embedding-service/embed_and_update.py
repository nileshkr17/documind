import asyncio
import asyncpg
import requests

import sys
import numpy as np

DB_HOST = "127.0.0.1"
DB_PORT = "5432"
DB_NAME = "ragbot"
DB_USER = "raguser"
DB_PASS = "ragpass"
EMBEDDING_API_URL = "http://127.0.0.1:8000/embed"


if len(sys.argv) < 2:
    print("Usage: python embed_and_update.py <document_id>")
    sys.exit(1)
document_id = sys.argv[1]

async def fetch_chunks():
    conn = await asyncpg.connect(
        host=DB_HOST, port=DB_PORT, database=DB_NAME, user=DB_USER, password=DB_PASS
    )
    rows = await conn.fetch(
        "SELECT id, chunk_text FROM document_chunks WHERE document_id = $1 ORDER BY chunk_index ASC", document_id
    )
    await conn.close()
    return [(str(row["id"]), row["chunk_text"]) for row in rows]

async def update_embeddings(chunk_ids, embeddings):
    conn = await asyncpg.connect(
        host=DB_HOST, port=DB_PORT, database=DB_NAME, user=DB_USER, password=DB_PASS
    )
    def to_pgvector_str(emb):
        return '[' + ','.join(f'{x:.6f}' for x in emb) + ']'
    for chunk_id, emb in zip(chunk_ids, embeddings):
        await conn.execute(
            "UPDATE document_chunks SET embedding = $1 WHERE id = $2",
            to_pgvector_str(emb), chunk_id
        )
    await conn.close()

def get_embeddings_from_api(chunks):
    response = requests.post(
        EMBEDDING_API_URL,
        json={"document_id": document_id, "chunks": chunks}
    )
    response.raise_for_status()
    return response.json()["embeddings"]

async def main():
    chunk_data = await fetch_chunks()
    chunk_ids = [cid for cid, _ in chunk_data]
    chunk_texts = [txt for _, txt in chunk_data]
    embeddings = get_embeddings_from_api(chunk_texts)
    await update_embeddings(chunk_ids, embeddings)
    print(f"Updated embeddings for {len(chunk_ids)} chunks.")

if __name__ == "__main__":
    asyncio.run(main())
