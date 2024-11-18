package com.hackthon.dms.controller;

import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) throws Exception {
        // Gerando uma chave para criptografar o arquivo
        String key = fileService.generateKey();

        // Criptografando o conteúdo do arquivo
        byte[] encryptedContent = fileService.encrypt(file.getBytes(), key);

        // Salvando o arquivo criptografado no banco de dados, passando o fileName
        fileService.saveFile(encryptedContent, key, fileName);

        // Retornando a chave de criptografia para o cliente
        return ResponseEntity.ok(key);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @RequestParam String key) throws Exception {
        // Recupera o arquivo criptografado
        EncryptedFile file = fileService.getFile(id);

        // Verifica se a chave fornecida é válida
        if (!file.getEncryptionKey().equals(key)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chave inválida".getBytes());
        }

        // Verifica se a chave não expirou
        if (file.getKeyExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("A chave expirou".getBytes());
        }

        // Descriptografa o conteúdo do arquivo
        byte[] content = fileService.decrypt(file.getEncryptedContent(), key);

        String contentType = file.getFileName().endsWith(".pdf") ? "application/pdf" : "application/octet-stream";

        // Definindo os cabeçalhos da resposta para o arquivo binário
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length));

        // Retorna o arquivo binário descriptografado
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
