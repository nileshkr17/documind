package com.rag.ragbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import java.util.UUID;

import jakarta.persistence.Table;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {
    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "chunk_index")
    private int chunkIndex;

    @Column(name = "chunk_text", columnDefinition = "TEXT")
    private String chunkText;

    @Column(name = "embedding", columnDefinition = "vector(384)", nullable = true)
    private float[] embedding;

    public DocumentChunk() {
        this.id = UUID.randomUUID();
    }

    public DocumentChunk(UUID documentId, int chunkIndex, String chunkText, float[] embedding) {
        this.id = UUID.randomUUID();
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.chunkText = chunkText;
        this.embedding = embedding;
    }

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getChunkText() { return chunkText; }
    public float[] getEmbedding() { return embedding; }

    public void setId(UUID id) { this.id = id; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
}