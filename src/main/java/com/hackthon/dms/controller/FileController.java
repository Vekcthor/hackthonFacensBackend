package com.hackthon.dms.controller;

import com.hackthon.dms.dto.EncryptedFileDTO;
import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public List<EncryptedFile> findAllFiles() {
        return fileService.listAllFiles();
    }

    @PostMapping("/upload")
    public ResponseEntity<EncryptedFileDTO> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) throws Exception {
        EncryptedFileDTO encryptedFileDTO = fileService.processUpload(file, fileName);
        return ResponseEntity.ok(encryptedFileDTO);
    }

    @GetMapping("/{randomIdentification}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long randomIdentification, @RequestParam String key)
            throws Exception {
        byte[] content = fileService.processDownloadAndGenerateHeaders(randomIdentification, key);
        return ResponseEntity.ok().body(content);
    }
}
