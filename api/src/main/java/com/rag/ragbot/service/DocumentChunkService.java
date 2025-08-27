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

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentChunkService {
    private final DocumentChunkRepository chunkRepository;

    @Autowired
    public DocumentChunkService(DocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public int processAndChunk(Document document) {
        String text = extractText(document.getPath());
        List<DocumentChunk> chunks = splitToChunks(document.getId(), text, 500, 800);
        chunkRepository.saveAll(chunks);
        return chunks.size();
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
            return "";
        }
    }

    private List<DocumentChunk> splitToChunks(UUID documentId, String text, int minSize, int maxSize) {
        List<DocumentChunk> chunks = new ArrayList<>();
        int index = 0;
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxSize, text.length());
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start + minSize) {
                    end = lastSpace;
                }
            }
            String chunkText = text.substring(start, end).trim();
            if (!chunkText.isEmpty()) {
                chunks.add(new DocumentChunk(documentId, index++, chunkText));
            }
            start = end;
        }
        return chunks;
    }
}
