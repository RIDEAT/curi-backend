package com.backend.curi.reports;

import com.backend.curi.member.controller.dto.MemberResponse;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentsResponse {
    private Long id;
    private String signedUrl;
    private String fileName;
    private MemberResponse member;
    private LocalDate responseDate;
    public static AttachmentsResponse of(Attachments attachment, String signedUrl){
        return AttachmentsResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .member(MemberResponse.of(attachment.getMember()))
                .signedUrl(signedUrl)
                .responseDate(attachment.getCreatedDate().toLocalDate())
                .build();
    }
}
