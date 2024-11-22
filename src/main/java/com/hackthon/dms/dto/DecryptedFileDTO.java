package com.hackthon.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecryptedFileDTO {
    private String fileName;
    private String recipientName;
    private byte[] decryptContent;
    // private String decryptContent;
}
