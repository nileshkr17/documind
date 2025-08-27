package com.rag.ragbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.rag.ragbot.model.DocumentChunk;

@Service
public class EmbeddingServiceClient {
    private final WebClient webClient;

    public EmbeddingServiceClient() {
        this.webClient = WebClient.builder()
            .baseUrl("http://127.0.0.1:8000")
            .build();
    }

    public void sendChunksForEmbedding(UUID documentId, List<DocumentChunk> chunks) {
        List<String> chunkTexts = chunks.stream()
            .map(DocumentChunk::getChunkText)
            .collect(Collectors.toList());
        System.out.println("[EMBED CLIENT] Calling embedding service for document_id: " + documentId);
        System.out.println("[EMBED CLIENT] Number of chunks: " + chunkTexts.size());
        if (!chunkTexts.isEmpty()) {
            System.out.println("[EMBED CLIENT] First chunk: " + chunkTexts.get(0));
        }
        webClient.post()
            .uri("/embed")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new EmbeddingRequest(documentId.toString(), chunkTexts))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    static class EmbeddingRequest {
        public String document_id;
        public List<String> chunks;
        public EmbeddingRequest(String document_id, List<String> chunks) {
            this.document_id = document_id;
            this.chunks = chunks;
        }
    }
}
