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
    private final String uploadDir = "uploads";

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    public Document store(MultipartFile file) throws IOException {
        String filePath = uploadDir + File.separator + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);
        Document doc = new Document(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            filePath
        );
        return documentRepository.save(doc);
    }
}
