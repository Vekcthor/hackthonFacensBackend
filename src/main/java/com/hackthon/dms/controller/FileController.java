package com.hackthon.dms.controller;

import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) throws Exception {
        // Processa o upload e retorna a chave
        String key = fileService.processUpload(file, fileName);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @RequestParam String key) throws Exception {
        // Processa o download
        byte[] content = fileService.processDownload(id, key);
        EncryptedFile file = fileService.getFile(id);

        // Configura cabeçalhos da resposta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, fileService.getContentType(file.getFileName()));
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length));

        return ResponseEntity.ok().headers(headers).body(content);
    }
}
