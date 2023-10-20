package com.backend.curi.reports;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.smtp.AwsS3Service;
import com.backend.curi.smtp.dto.PreSignedUrl;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.contents.AttachmentContent;
import com.backend.curi.workflow.service.ContentService;
import com.backend.curi.workflow.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentsService {
    private final AttachmentsRepository attachmentsRepository;
    private final AwsS3Service amazonS3Service;
    private final ModuleService moduleService;
    private static final String PATH_FORMAT = "workspace/%s/members/%s/modules/%s/%s";

    public PreSignedUrl getPreSignedUrl(String fileName, LaunchedModule launchedModule, Content content){
        var workspaceId = launchedModule.getWorkspace().getId();
        var memberId = launchedModule.getLaunchedSequence().getMember().getId();
        var module = launchedModule.getModule();

        if (module.getType() != ModuleType.attachments)
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.MODULE_TYPE_NOT_MATCH);

        var attachmentsInfo = (AttachmentContent) content.getContent();

        if (!amazonS3Service.isValidAttachmentName(fileName, attachmentsInfo.getExtensions()))
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_FILE_EXTENSION);


        var path = attachmentFormat(workspaceId.toString(), memberId.toString(), module.getId().toString(), fileName);
        return PreSignedUrl.builder()
                .preSignedUrl(amazonS3Service.getPreSignedUrl(path))
                .fileName(fileName).build();
    }

    @Transactional
    public AttachmentsResponse createAttachments(AttachmentsRequest attachmentsRequest, LaunchedModule launchedModule) {
        var workspaceId =launchedModule.getWorkspace().getId();
        var module = launchedModule.getModule();
        var member = launchedModule.getLaunchedSequence().getMember();
        var path = attachmentFormat(workspaceId.toString(), member.getId().toString(), module.getId().toString(), attachmentsRequest.getFileName());
        Attachments attachments = Attachments.of(module,launchedModule,member, path, attachmentsRequest.getFileName());
        attachmentsRepository.save(attachments);
        var signedUrl = amazonS3Service.getSignedUrl(path);
        return AttachmentsResponse.of(attachments, signedUrl);
    }

    public AttachmentsResponse getAttachment(LaunchedModule launchedModule){
        var attachments = attachmentsRepository.findByLaunchedModule(launchedModule).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.ATTACHMENTS_NOT_EXISTS));
        var signedUrl = amazonS3Service.getSignedUrl(attachments.getResourceUrl());
        return AttachmentsResponse.of(attachments, signedUrl);
    }

    public List<AttachmentsResponse> getAttachments(Long moduleId){
        var module = moduleService.getModuleEntity(moduleId);
        var attachments = attachmentsRepository.findAllByModule(module);
        return attachments.stream().map(attachment -> AttachmentsResponse.of(attachment, "")).collect(Collectors.toList());
    }

    private String attachmentFormat(String workspaceId, String memberId, String moduleId, String fileName) {
        return String.format(PATH_FORMAT, workspaceId, memberId, moduleId, fileName);
    }
}
