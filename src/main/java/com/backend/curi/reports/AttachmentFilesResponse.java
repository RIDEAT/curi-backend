package com.backend.curi.reports;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentFilesResponse {
    private String signedUrl;
    private String resourceUrl;
    private String fileName;

    public static AttachmentFilesResponse of (String signedUrl, AttachmentsInfo info) {
        return AttachmentFilesResponse.builder()
                .signedUrl(signedUrl)
                .resourceUrl(info.getResourceUrl())
                .fileName(info.getFileName())
                .build();
    }
}
