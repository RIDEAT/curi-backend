package com.backend.curi.workflow.repository.entity.contents;

import com.backend.curi.reports.AttachmentsInfo;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentContent {
    @Builder.Default
    private List<AttachmentsInfo> attachments = new ArrayList<>();
    private String description;
}
