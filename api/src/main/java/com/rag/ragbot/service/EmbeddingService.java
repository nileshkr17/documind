package com.rag.ragbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${embedding.api.url}")
    private String embeddingApiUrl;

    public float[] generateEmbedding(String text) {
        // Assumes Python API returns a float array as JSON
        return restTemplate.postForObject(embeddingApiUrl, java.util.Map.of("text", text), float[].class);
    }
}
