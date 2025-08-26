package com.rag.ragbot.controller;

import com.rag.ragbot.model.Document;
import com.rag.ragbot.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }
            String uploadDir = "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String filePath = System.getProperty("user.dir") + File.separator + uploadDir + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            Document doc = new Document(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                filePath
            );
            documentRepository.save(doc);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            logger.error("Failed to upload document", e);
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
}
