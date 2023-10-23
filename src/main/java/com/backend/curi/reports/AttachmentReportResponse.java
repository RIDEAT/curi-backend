package com.backend.curi.reports;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentReportResponse {
    private Long id;
    private String workflowTitle;
    private String moduleTitle;
    private Long attachCnt;
    private List<AttachmentsResponse> attachments;
}
