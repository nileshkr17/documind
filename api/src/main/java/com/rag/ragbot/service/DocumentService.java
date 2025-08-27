package com.rag.ragbot.service;

import com.rag.ragbot.model.Document;
import com.rag.ragbot.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentChunkService documentChunkService;
    private final String uploadDir = "uploads";

    @Autowired
    public DocumentService(DocumentRepository documentRepository, DocumentChunkService documentChunkService) {
        this.documentRepository = documentRepository;
        this.documentChunkService = documentChunkService;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    public Document store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String safeFilename = sanitizeFilename(originalFilename);
        String filePath = uploadDir + File.separator + safeFilename;
        File dest = new File(filePath);
        file.transferTo(dest);
        Document doc = new Document(
            safeFilename,
            file.getContentType(),
            file.getSize(),
            filePath
        );
        doc = documentRepository.save(doc);
        // Call chunking after saving document
        documentChunkService.processAndChunk(doc);
        return doc;
    }
    /**
     * Sanitizes a filename by removing path separators and allowing only safe characters.
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed";
        }
        // Remove any path components
        filename = filename.replaceAll("\\\\", "/");
        int lastSlash = filename.lastIndexOf('/');
        if (lastSlash >= 0) {
            filename = filename.substring(lastSlash + 1);
        }
        // Allow only alphanumerics, dot, underscore, and hyphen
        filename = filename.replaceAll("[^A-Za-z0-9._-]", "_");
        if (filename.isEmpty()) {
            return "unnamed";
        }
        return filename;
    }
}
