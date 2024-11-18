package com.hackthon.dms.controller;

import com.hackthon.dms.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.encryptAndSaveFile(file);
            return "File uploaded and encrypted successfully.";
        } catch (Exception e) {
            return "File upload failed: " + e.getMessage();
        }
    }
}

