import asyncio
import asyncpg
import numpy as np

# Database connection details
DB_HOST = "127.0.0.1"
DB_PORT = "5432"
DB_NAME = "ragbot"
DB_USER = "raguser"
DB_PASS = "ragpass"

async def update_embeddings(chunk_id, embedding):
    conn = await asyncpg.connect(
        host=DB_HOST, port=DB_PORT, database=DB_NAME, user=DB_USER, password=DB_PASS
    )
    await conn.execute(
        "UPDATE document_chunks SET embedding = $1 WHERE id = $2",
        np.array(embedding, dtype=np.float32).tolist(), chunk_id
    )
    await conn.close()

# Example usage:
# asyncio.run(update_embeddings('your-chunk-uuid', [0.1, 0.2, ...]))  # 384 floats
