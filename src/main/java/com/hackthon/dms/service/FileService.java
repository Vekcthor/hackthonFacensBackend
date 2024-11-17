package com.hackthon.dms.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public void encryptAndSaveFile(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();

        // Encryption using AES
        Key aesKey = generateAESKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // Save encrypted bytes to DB
        EncryptedFile encryptedFile = new EncryptedFile(file.getOriginalFilename(), encryptedBytes);
        fileRepository.save(encryptedFile);
    }

    private Key generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // For AES-128 encryption
        return keyGen.generateKey();
    }
}
