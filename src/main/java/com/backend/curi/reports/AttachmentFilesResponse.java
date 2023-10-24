package com.backend.curi.reports;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentFilesResponse {
    private String signedUrl;
    private String fileName;

    public static AttachmentFilesResponse of (String signedUrl, String fileName) {
        return AttachmentFilesResponse.builder()
                .signedUrl(signedUrl)
                .fileName(fileName)
                .build();
    }
}
