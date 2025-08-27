package com.rag.ragbot.controller;

import com.rag.ragbot.model.Document;
import com.rag.ragbot.repository.DocumentRepository;
import com.rag.ragbot.service.DocumentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentProcessingService documentProcessingService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }
            String uploadDir = "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            // Generate a unique filename to prevent overwriting
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
                originalFilename = originalFilename.substring(0, dotIndex);
            }
            String uniqueFilename = originalFilename + "_" + UUID.randomUUID().toString() + fileExtension;
            String filePath = System.getProperty("user.dir") + File.separator + uploadDir + File.separator + uniqueFilename;
            file.transferTo(new File(filePath));
            Document doc = new Document(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                filePath
            );
            documentRepository.save(doc);
            int chunkCount = documentProcessingService.processAndChunk(doc);
            String docType = "unknown";
            if (doc.getType() != null) {
                if (doc.getType().contains("pdf")) docType = "pdf";
                else if (doc.getType().contains("wordprocessingml")) docType = "docx";
                else if (doc.getType().contains("text/plain")) docType = "txt";
            }
            return ResponseEntity.ok(
                java.util.Map.of(
                    "document", doc,
                    "chunkCount", chunkCount,
                    "documentType", docType
                )
            );
        } catch (Exception e) {
            logger.error("Failed to upload document", e);
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
}
