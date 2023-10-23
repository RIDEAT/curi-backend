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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentsService {
    private final AttachmentsRepository attachmentsRepository;
    private final AwsS3Service amazonS3Service;
    private final ModuleService moduleService;
    private static final String PATH_FORMAT = "workspace/%s/members/%s/modules/%s/%s";

    public List<PreSignedUrl> getPreSignedUrls(LaunchedModule launchedModule, Content content) {
        var workspaceId = launchedModule.getWorkspace().getId();
        var memberId = launchedModule.getLaunchedSequence().getMember().getId();
        var module = launchedModule.getModule();

        if (module.getType() != ModuleType.attachments)
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.MODULE_TYPE_NOT_MATCH);

        var attachmentsInfos = (AttachmentContent) content.getContent();

        return attachmentsInfos.getAttachmentsInfos().stream()
                .map(info -> getPreSignedUrl(workspaceId, memberId, module.getId(), info))
                .collect(Collectors.toList());
    }

    private PreSignedUrl getPreSignedUrl(Long workspaceId, Long memberId, Long moduleId, AttachmentsInfo info){
        var fileName = info.getFileName();
        var extensions = info.getExtensions();
        if (!amazonS3Service.isValidAttachmentName(fileName, extensions))
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_FILE_EXTENSION);

        var path = attachmentFormat(workspaceId.toString(), memberId.toString(), moduleId.toString(), fileName);
        return PreSignedUrl.builder()
                .preSignedUrl(amazonS3Service.getPreSignedUrl(path))
                .fileName(fileName).build();
    }

    @Transactional
    public List<AttachmentsResponse> createAttachments(List<AttachmentsRequest> attachmentsRequest, LaunchedModule launchedModule) {
        var workspaceId = launchedModule.getWorkspace().getId();
        var module = launchedModule.getModule();
        var member = launchedModule.getLaunchedSequence().getMember();

        var responses = new ArrayList<AttachmentsResponse>();
        for (var request : attachmentsRequest) {
            var path = attachmentFormat(workspaceId.toString(), member.getId().toString(), module.getId().toString(), request.getFileName());
            Attachments attachments = Attachments.of(module, launchedModule, member, path, request.getFileName());
            attachmentsRepository.save(attachments);
            var signedUrl = amazonS3Service.getSignedUrl(path);
            responses.add(AttachmentsResponse.of(attachments, signedUrl));
        }
        return responses;
    }

    public List<AttachmentsResponse> getAttachment(LaunchedModule launchedModule) {
        var attachments = launchedModule.getAttachments();
        if (attachments.isEmpty())
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.ATTACHMENTS_NOT_EXISTS);
        return attachments.stream()
                .map(attachment ->
                        AttachmentsResponse.of(attachment, amazonS3Service.getSignedUrl(attachment.getResourceUrl())))
                .collect(Collectors.toList());
    }

    public List<AttachmentsResponse> getAttachments(Long moduleId) {
        var module = moduleService.getModuleEntity(moduleId);
        var attachments = attachmentsRepository.findAllByModule(module);
        return attachments.stream().map(attachment -> AttachmentsResponse.of(attachment, "")).collect(Collectors.toList());
    }

    private String attachmentFormat(String workspaceId, String memberId, String moduleId, String fileName) {
        return String.format(PATH_FORMAT, workspaceId, memberId, moduleId, fileName);
    }
}
