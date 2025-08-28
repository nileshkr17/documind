package com.rag.ragbot.controller;

import com.rag.ragbot.service.DocumentChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chunks")
public class ChunkController {
    @Autowired
    private DocumentChunkService chunkService;

    @DeleteMapping
    public ResponseEntity<?> deleteAllChunks() {
        try {
            chunkService.deleteAllChunks();
            return ResponseEntity.ok("All chunks deleted");
        } catch (Exception e) {
            // Log the error with stack trace
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete chunks: " + e.getMessage());
        }
    }
}
