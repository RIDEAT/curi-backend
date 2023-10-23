package com.backend.curi.reports;

import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.member.controller.dto.MemberResponse;
import com.backend.curi.member.repository.entity.Member;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentsResponse {
    private Long id;
    private List<AttachmentFilesResponse> attachmentFiles;
    private MemberResponse member;

    public static AttachmentsResponse of(LaunchedModule launchedModule, List<AttachmentFilesResponse> attachmentFiles, Member member){
        return AttachmentsResponse.builder()
                .id(launchedModule.getId())
                .attachmentFiles(attachmentFiles)
                .member(MemberResponse.of(member))
                .build();
    }
}
