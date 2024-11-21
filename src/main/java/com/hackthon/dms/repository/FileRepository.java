package com.hackthon.dms.repository;

import com.hackthon.dms.model.EncryptedFile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<EncryptedFile, Long> {
    Optional<EncryptedFile> findByRandomIdentification(Long randomIdentification);
}

