package com.rag.ragbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Document {
    @Id
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private String path;

    public Document() {
        this.id = UUID.randomUUID();
    }

    public Document(String name, String type, Long size, String path) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.size = size;
        this.path = path;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Long getSize() { return size; }
    public String getPath() { return path; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setSize(Long size) { this.size = size; }
    public void setPath(String path) { this.path = path; }
}
