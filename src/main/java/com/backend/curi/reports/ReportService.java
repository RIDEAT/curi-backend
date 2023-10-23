package com.backend.curi.reports;

import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.workflow.repository.ModuleRepository;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final AttachmentsService attachmentsService;
    private final ModuleRepository moduleRepository;
    private final WorkspaceService workspaceService;

    public List<AttachmentReportResponse> getAttachmentReport(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var modules = moduleRepository.findAllByWorkspaceAndType(workspace, ModuleType.attachments);

        return modules.stream().map(module -> {
            var attachments = attachmentsService.getAttachments(module.getId());
            var attachCnt = (long) attachments.size();
            if(attachCnt == 0)
                return AttachmentReportResponse.builder()
                        .id(module.getId())
                        .workflowTitle(module.getSequence().getWorkflow().getName())
                        .moduleTitle(module.getName())
                        .attachments(attachments)
                        .attachCnt(attachCnt)
                        .lastAttachDate(null)
                        .build();
            return AttachmentReportResponse.builder()
                    .id(module.getId())
                    .workflowTitle(module.getSequence().getWorkflow().getName())
                    .moduleTitle(module.getName())
                    .attachments(attachments)
                    .attachCnt(attachCnt)
                    .lastAttachDate(attachments.get(attachments.size()-1).getResponseDate())
                    .build();
        }).toList();
    }
}
