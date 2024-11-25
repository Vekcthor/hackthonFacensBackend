package com.hackthon.dms.controller;

import com.hackthon.dms.dto.DecryptedFileDTO;
import com.hackthon.dms.dto.EncryptedFileDTO;
import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public List<EncryptedFile> findAllFiles() {
        return fileService.listAllFiles();
    }

    @PostMapping("/upload")
    public ResponseEntity<EncryptedFileDTO> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName, @RequestParam("recipientName") String recipientName,
            @RequestParam("passphrase") String passphrase)
            throws Exception {
        EncryptedFileDTO encryptedFileDTO = fileService.processUpload(file, fileName, recipientName, passphrase);
        return ResponseEntity.ok(encryptedFileDTO);
    }

    @GetMapping("/{randomIdentification}")
    public ResponseEntity<DecryptedFileDTO> downloadFile(@PathVariable Long randomIdentification,
            @RequestParam("key") String key, @RequestParam("passphrase") String passphrase)
            throws Exception {
        DecryptedFileDTO content = fileService.processDownloadAndGenerateHeaders(randomIdentification, key, passphrase);
        return ResponseEntity.ok().body(content);
    }
}
