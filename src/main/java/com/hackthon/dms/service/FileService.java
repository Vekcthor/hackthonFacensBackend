package com.hackthon.dms.service;

import com.hackthon.dms.exception.GeneralApiError;
import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private static final String ALGORITHM = "AES";

    public List<EncryptedFile> listAllFiles(){
        return fileRepository.findAll();
    }

    public String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public byte[] encrypt(byte[] data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    public EncryptedFile saveFile(byte[] encryptedContent, String key, String fileName) {
        EncryptedFile file = new EncryptedFile();
        file.setEncryptedContent(encryptedContent);
        file.setEncryptionKey(key);
        file.setKeyExpiration(LocalDateTime.now().plusMinutes(30));
        file.setFileName(fileName);
        return fileRepository.save(file);
    }

    public EncryptedFile getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new GeneralApiError("File not found in the base"));
    }

    public String processUpload(MultipartFile file, String fileName) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new GeneralApiError("File is required and cannot be empty.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new GeneralApiError("File Name is required and cannot be empty.");
        }
        String key = generateKey();
        byte[] encryptedContent = encrypt(file.getBytes(), key);
        saveFile(encryptedContent, key, fileName);
        return key;
    }

    public byte[] processDownload(Long id, String key) throws Exception {
        EncryptedFile file = getFile(id);

        if (!file.getEncryptionKey().equals(key)) {
            throw new GeneralApiError("Invalid key");
        }

        if (file.getKeyExpiration().isBefore(LocalDateTime.now())) {
            throw new GeneralApiError("Key has expired");
        }

        return decrypt(file.getEncryptedContent(), key);
    }

    public String getContentType(String fileName) {
        return fileName.endsWith(".pdf") ? "application/pdf" : "application/octet-stream";
    }
}
