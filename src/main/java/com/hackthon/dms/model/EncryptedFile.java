package com.hackthon.dms.model;

import jakarta.persistence.*;

@Entity
public class EncryptedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Lob
    private byte[] fileData;

    public EncryptedFile() {}

    public EncryptedFile(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    // Getters and Setters
}
