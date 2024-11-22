package com.hackthon.dms.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tbl_encrypted_files")
public class EncryptedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="random_identification")
    private Long randomIdentification;

    @Column(name="file_name")
    private String fileName;

    @Column(name="recipient_name")
    private String recipientName;

    @Lob
    @Column(name="encrypted_content")
    private byte[] encryptedContent;

    @Column(name="encryption_key")
    private String encryptionKey;

    @Column(name="key_expiration")
    private LocalDateTime keyExpiration;
}
