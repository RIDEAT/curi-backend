package com.backend.curi.reports;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/attachments")
    public ResponseEntity<List<AttachmentReportResponse>> getAttachmentReport(@PathVariable Long workspaceId){
        var response = reportService.getAttachmentReport(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/attachments/{launchedModuleId}")
    public ResponseEntity<AttachmentsResponse> getAttachment(@PathVariable Long workspaceId, @PathVariable Long launchedModuleId){
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getAttachment(launchedModuleId));
    }

}
