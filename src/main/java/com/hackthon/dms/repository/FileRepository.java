package com.hackthon.dms.repository;

import com.hackthon.dms.model.EncryptedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<EncryptedFile, Long> {
}

