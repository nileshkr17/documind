package com.rag.ragbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.util.UUID;

@Entity
public class DocumentChunk {
    @Id
    private UUID id;
    private UUID documentId;
    private int chunkIndex;
    @Column(columnDefinition = "TEXT")
    private String content;

    public DocumentChunk() {
        this.id = UUID.randomUUID();
    }

    public DocumentChunk(UUID documentId, int chunkIndex, String content) {
        this.id = UUID.randomUUID();
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
    }

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }

    public void setId(UUID id) { this.id = id; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public void setContent(String content) { this.content = content; }
}