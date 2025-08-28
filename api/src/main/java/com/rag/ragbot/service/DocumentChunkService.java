
package com.rag.ragbot.service;

import com.rag.ragbot.model.Document;
import com.rag.ragbot.model.DocumentChunk;
import com.rag.ragbot.repository.DocumentChunkRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentChunkService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentChunkService.class);
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    @Autowired
    public DocumentChunkService(DocumentChunkRepository chunkRepository, EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }
    // Save chunks with embeddings
    public void saveChunksWithEmbeddings(UUID documentId, List<String> chunks) {
        int idx = 0;
        for (String chunk : chunks) {
            float[] embedding = embeddingService.generateEmbedding(chunk);
            DocumentChunk docChunk = new DocumentChunk(documentId, idx++, chunk, embedding);
            chunkRepository.save(docChunk);
        }
    }

    public int processAndChunk(Document document) {
        String text = extractText(document.getPath());
        List<String> chunkTexts = splitTextToChunks(text, 500, 800);
        saveChunksWithEmbeddings(document.getId(), chunkTexts);
        return chunkTexts.size();
    }

    private String extractText(String filePath) {
        try {
            if (filePath.endsWith(".pdf")) {
                try (PDDocument pdf = PDDocument.load(new File(filePath))) {
                    return new PDFTextStripper().getText(pdf);
                }
            } else if (filePath.endsWith(".docx")) {
                try (FileInputStream fis = new FileInputStream(filePath);
                     XWPFDocument docx = new XWPFDocument(fis)) {
                    StringBuilder sb = new StringBuilder();
                    for (XWPFParagraph p : docx.getParagraphs()) {
                        sb.append(p.getText()).append("\n");
                    }
                    return sb.toString();
                }
            } else {
                return Files.readString(new File(filePath).toPath());
            }
        } catch (Exception e) {
            logger.error("Failed to extract text from file: {}", filePath, e);
            return "";
        }
    }
    // Split text into chunks of 500-800 chars, not breaking words
    private List<String> splitTextToChunks(String text, int minSize, int maxSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxSize, text.length());
            // Try to break at a word boundary
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start + minSize) {
                    end = lastSpace;
                }
            }
            String chunkText = text.substring(start, end).trim();
            if (!chunkText.isEmpty()) {
                chunks.add(chunkText);
            }
            start = end;
        }
        return chunks;
    }
    // Delete all chunks
    public void deleteAllChunks() {
        try {
            chunkRepository.deleteAll();
        } catch (Exception e) {
            logger.error("Error deleting all chunks", e);
            throw e;
        }
    }
}
