package com.backend.curi.reports;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.smtp.AwsS3Service;
import com.backend.curi.smtp.dto.PreSignedUrl;
import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.contents.AttachmentContent;
import com.backend.curi.workflow.service.ContentService;
import com.backend.curi.workflow.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentsService {
    private final ContentService contentService;
    private final AwsS3Service amazonS3Service;
    private final ModuleService moduleService;
    private static final String PATH_FORMAT = "workspace/%s/members/%s/modules/%s/%s";

    public List<PreSignedUrl> getPreSignedUrls(LaunchedModule launchedModule, List<AttachmentsRequest> presignedRequest) {
        var workspaceId = launchedModule.getWorkspace().getId();
        var memberId = launchedModule.getLaunchedSequence().getMember().getId();

        if (launchedModule.getType() != ModuleType.attachments)
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.MODULE_TYPE_NOT_MATCH);

        var content = contentService.getContent(launchedModule.getContentId());
        var attachmentsInfos = (AttachmentContent) content.getContent();

        return presignedRequest.stream()
                .map(info -> getPreSignedUrl(workspaceId, memberId, launchedModule.getId(), info, attachmentsInfos.getExtensions()))
                .collect(Collectors.toList());
    }

    private PreSignedUrl getPreSignedUrl(Long workspaceId, Long memberId, Long moduleId, AttachmentsRequest request, List<String> extensions){
        var fileName = request.getFileName();
        if (!amazonS3Service.isValidAttachmentName(fileName, extensions))
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_FILE_EXTENSION);

        var path = attachmentFormat(workspaceId.toString(), memberId.toString(), moduleId.toString(), fileName);
        return PreSignedUrl.builder()
                .preSignedUrl(amazonS3Service.getPreSignedUrl(path))
                .fileName(fileName).build();
    }

    @Transactional
    public ContentResponse createAttachments(List<AttachmentsRequest> attachmentsRequest, LaunchedModule launchedModule) {
        var workspaceId = launchedModule.getWorkspace().getId();
        var member = launchedModule.getLaunchedSequence().getMember();
        var content = contentService.getContent(launchedModule.getContentId());
        var attachmentsInfos = (AttachmentContent) content.getContent();
        attachmentsInfos.setAttachments(new ArrayList<>());
        for (var request : attachmentsRequest) {
            var path = attachmentFormat(workspaceId.toString(), member.getId().toString(), launchedModule.getId().toString(), request.getFileName());
            attachmentsInfos.getAttachments().add(
                    AttachmentsInfo.builder()
                    .fileName(request.getFileName())
                    .resourceUrl(path)
                    .build());
        }
        content.setContent(attachmentsInfos);
        return ContentResponse.of(content, launchedModule);
    }

    private List<AttachmentFilesResponse> getFileResponses(AttachmentContent content){
        return content.getAttachments().stream().map(info ->{
            var signedUrl = amazonS3Service.getSignedUrl(info.getResourceUrl());
            return AttachmentFilesResponse.of(signedUrl, info);
        }).toList();
    }

    public AttachmentsResponse getAttachment(LaunchedModule launchedModule) {
        var content = contentService.getContent(launchedModule.getContentId());
        var attachmentsInfos = (AttachmentContent) content.getContent();
        return AttachmentsResponse.of(launchedModule, getFileResponses(attachmentsInfos), launchedModule.getLaunchedSequence().getMember());
    }

    public List<AttachmentsResponse> getAttachmentsByModule(Long moduleId) {
        var module = moduleService.getModuleEntity(moduleId);
        var launchedModules = module.getLaunchedModules();
        return launchedModules.stream().map(this::getAttachment).toList();
    }

    private String attachmentFormat(String workspaceId, String memberId, String moduleId, String fileName) {
        return String.format(PATH_FORMAT, workspaceId, memberId, moduleId, fileName);
    }
}
