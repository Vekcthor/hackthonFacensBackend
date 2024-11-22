package com.hackthon.dms.service;

import com.hackthon.dms.dto.DecryptedFileDTO;
import com.hackthon.dms.dto.EncryptedFileDTO;
import com.hackthon.dms.exception.GeneralApiError;
import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.repository.FileRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private final Random random = new Random();
    private final Set<Long> generatedNumbers = new HashSet<>();

    private static final String ALGORITHM = "AES";

    public List<EncryptedFile> listAllFiles() {
        return fileRepository.findAll();
    }

    private String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return Base64.getUrlEncoder().encodeToString(key.getEncoded());
    }

    private byte[] encrypt(byte[] data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getUrlDecoder().decode(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    private byte[] decrypt(byte[] data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getUrlDecoder().decode(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    private EncryptedFile saveFile(byte[] encryptedContent, String key, String fileName, Long randomIdentification,
            String recipientName) {
        EncryptedFile file = new EncryptedFile();
        file.setRandomIdentification(randomIdentification);
        file.setFileName(fileName);
        file.setRecipientName(recipientName);
        file.setEncryptedContent(encryptedContent);
        file.setEncryptionKey(key);
        file.setKeyExpiration(LocalDateTime.now().plusMinutes(30));
        return fileRepository.save(file);
    }

    public EncryptedFile getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new GeneralApiError("File not found in the base"));
    }

    public EncryptedFile getFileByRandomIdentification(Long randomIdentification) {
        return fileRepository.findByRandomIdentification(randomIdentification)
                .orElseThrow(() -> new GeneralApiError("File not found in the base"));
    }

    private Long processRandomIdentification() {
        Long randomNumber;
        do {
            randomNumber = 3 + Math.abs(random.nextLong() % 100);
        } while (generatedNumbers.contains(randomNumber));
        generatedNumbers.add(randomNumber);
        return randomNumber;
    }

    private void validateFileAndFileName(MultipartFile file, String fileName, String recipientName) {
        if (file == null || file.isEmpty()) {
            throw new GeneralApiError("File is required and cannot be empty.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new GeneralApiError("File Name is required and cannot be empty.");
        }

        if (recipientName == null || recipientName.isBlank()) {
            throw new GeneralApiError("Recipient Name is required and cannot be empty.");
        }
    }

    private EncryptedFileDTO buildEncryptedFileDTO(Long randomIdentification, String key) {
        return EncryptedFileDTO.builder()
                .randomIdentification(randomIdentification)
                .encryptionKey(key)
                .build();
    }

    public EncryptedFileDTO processUpload(MultipartFile file, String fileName, String recipientName) throws Exception {
        validateFileAndFileName(file, fileName, recipientName);
        Long randomIdentification = processRandomIdentification();
        String key = generateKey();
        byte[] encryptedContent = encrypt(file.getBytes(), key);
        saveFile(encryptedContent, key, fileName, randomIdentification, recipientName);
        return buildEncryptedFileDTO(randomIdentification, key);
    }

    void validateFileKey(EncryptedFile file, String key) {
        if (!file.getEncryptionKey().equals(key)) {
            throw new GeneralApiError("Invalid key");
        }

        if (file.getKeyExpiration().isBefore(LocalDateTime.now())) {
            throw new GeneralApiError("Key has expired");
        }
    }

    private void buildHeaders(HttpHeaders headers, EncryptedFile file, int contentLength) {
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, getContentType(file.getFileName()));
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public DecryptedFileDTO processDownloadAndGenerateHeaders(Long randomIdentification, String key) throws Exception {
        EncryptedFile file = getFileByRandomIdentification(randomIdentification);
        validateFileKey(file, key);
        byte[] content = decrypt(file.getEncryptedContent(), key);
        HttpHeaders headers = new HttpHeaders();
        buildHeaders(headers, file, content.length);
        fileRepository.deleteById(file.getId());
        return buildDecryptedFileDTO(content, file);
    }

    private DecryptedFileDTO buildDecryptedFileDTO(byte[] decryptContent, EncryptedFile file) {
        // String content = new String(decryptContent, StandardCharsets.UTF_8);
        return DecryptedFileDTO.builder()
                .fileName(file.getFileName())
                .recipientName(file.getRecipientName())
                .decryptContent(decryptContent)
                .build();
    }

    public String getContentType(String fileName) {
        return fileName.endsWith(".pdf") ? "application/pdf" : "application/octet-stream";
    }
}
