package com.rag.ragbot.repository;

import com.rag.ragbot.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
	List<DocumentChunk> findByDocumentId(UUID documentId);

	// Native query for similarity search using pgvector
	@Query(value = "SELECT * FROM document_chunks ORDER BY embedding <-> CAST(:embedding AS vector) LIMIT :limit", nativeQuery = true)
	List<DocumentChunk> findSimilarChunks(@Param("embedding") String embedding, @Param("limit") int limit);
}
