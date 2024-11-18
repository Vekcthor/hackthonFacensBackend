package com.hackthon.dms.service;

import com.hackthon.dms.model.EncryptedFile;
import com.hackthon.dms.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

     private static final String ALGORITHM = "AES";

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
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
