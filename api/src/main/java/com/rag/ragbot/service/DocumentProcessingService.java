package com.rag.ragbot.service;

import com.rag.ragbot.model.Document;
import com.rag.ragbot.model.DocumentChunk;
import com.rag.ragbot.repository.DocumentChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Service
public class DocumentProcessingService {
    private final DocumentChunkRepository chunkRepository;

    @Autowired
    public DocumentProcessingService(DocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public int processAndChunk(Document document) {
        String text = extractText(document.getPath());
        List<DocumentChunk> chunks = splitToChunks(document.getId(), text, 500);
        chunkRepository.saveAll(chunks);
        return chunks.size();
    }

    private String extractText(String filePath) {
        try {
            if (filePath.endsWith(".txt")) {
                File file = new File(filePath);
                return java.nio.file.Files.readString(file.toPath());
            } else if (filePath.endsWith(".docx")) {
                try (FileInputStream fis = new FileInputStream(filePath);
                     XWPFDocument doc = new XWPFDocument(fis);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else if (filePath.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(new File(filePath))) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            logger.error("Failed to extract text from file: {}", filePath, e);
            return "";
        }
    }

    private List<DocumentChunk> splitToChunks(UUID documentId, String text, int chunkSize) {
        List<DocumentChunk> chunks = new ArrayList<>();
        int index = 0;
        for (int start = 0; start < text.length(); start += chunkSize) {
            int end = Math.min(start + chunkSize, text.length());
            String chunkText = text.substring(start, end);
            chunks.add(new DocumentChunk(documentId, index++, chunkText));
        }
        return chunks;
    }
}
