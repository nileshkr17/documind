package com.rag.ragbot.repository;

import com.rag.ragbot.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
}
