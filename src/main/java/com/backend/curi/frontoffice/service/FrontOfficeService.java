package com.backend.curi.frontoffice.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.controller.dto.FrontOfficeResponse;
import com.backend.curi.frontoffice.controller.dto.LaunchedModuleWithContent;
import com.backend.curi.frontoffice.controller.dto.SequenceSatisfactionRequest;
import com.backend.curi.frontoffice.controller.dto.SequenceSatisfactionResponse;
import com.backend.curi.frontoffice.repository.FrontOfficeRepository;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.launched.service.LaunchedSequenceService;
import com.backend.curi.reports.AttachmentsRequest;
import com.backend.curi.reports.AttachmentsResponse;
import com.backend.curi.reports.AttachmentsService;
import com.backend.curi.slack.controller.dto.OAuthRequest;

import com.backend.curi.slack.service.SlackService;

import com.backend.curi.smtp.dto.PreSignedUrl;
import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.repository.SequenceSatisfactionRepository;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.SequenceSatisfaction;
import com.backend.curi.workflow.service.ContentService;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FrontOfficeService {

    private final SlackService slackService;
    private final FrontOfficeRepository frontOfficeRepository;
    private final LaunchedModuleService launchedModuleService;
    private final LaunchedSequenceService launchedSequenceService;
    private final ContentService contentService;
    private final SequenceSatisfactionRepository sequenceSatisfactionRepository;
    private final AttachmentsService attachmentService;

    public FrontOfficeResponse getFrontOffice(UUID frontOfficeId) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        return FrontOfficeResponse.of(frontOffice);
    }

    public LaunchedModuleWithContent getLaunchedModuleWithContent(Long launchedModuleId){
        LaunchedModule launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
        ObjectId contentId = launchedModule.getContentId();
        Content content = contentService.getContent(contentId);
        return LaunchedModuleWithContent.of(LaunchedModuleResponse.of(launchedModule), ContentResponse.of(content, launchedModule));
    }

    public LaunchedModuleWithContent completeLaunchedModuleWithContent(Long launchedModuleId) {
        LaunchedModule launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
        ObjectId contentId = launchedModule.getContentId();
        Content content = contentService.getContent(contentId);
        LaunchedModule completedModule = launchedModuleService.completeLaunchedModule(launchedModule);
        checkIfAllModulesCompleted(launchedModule);
        return LaunchedModuleWithContent.of(LaunchedModuleResponse.of(completedModule), ContentResponse.of(content, launchedModule));
    }

    private void checkIfAllModulesCompleted(LaunchedModule launchedModule) {
        LaunchedSequence launchedSequence = launchedModule.getLaunchedSequence();
        if (launchedSequence.getLaunchedModules().stream().allMatch(launchedModule1 -> launchedModule1.getStatus().equals(LaunchedStatus.COMPLETED))) {
            launchedSequenceService.completeLaunchedSequence(launchedSequence);
        }
    }

    public LaunchedModuleWithContent startLaunchedModuleWithContent(Long launchedModuleId) {
        LaunchedModule launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
        ObjectId contentId = launchedModule.getContentId();
        Content content = contentService.getContent(contentId);
        LaunchedModule startedModule = launchedModuleService.startLaunchedModule(launchedModule);
        return LaunchedModuleWithContent.of(LaunchedModuleResponse.of(startedModule), ContentResponse.of(content, launchedModule));
    }

    public void checkAuth(UUID frontOfficeId, UUID accessToken) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        if (!frontOffice.getAccessToken().equals(accessToken)) throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.FRONTOFFICE_UNAUTHORIZED);

    }

    public void createFrontOffice(LaunchedSequence launchedSequence) {
        FrontOffice frontOffice = new FrontOffice();
        frontOffice.setLaunchedSequence(launchedSequence);
        frontOffice.setAccessToken(UUID.randomUUID());
        frontOfficeRepository.save(frontOffice);

        launchedSequence.setFrontOffice(frontOffice);


    }

    public FrontOffice getFrontOfficeByLaunchedSequenceId(Long launchedSequenceId) {
        return frontOfficeRepository.findByLaunchedSequenceId(launchedSequenceId).orElseThrow(()-> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
    }

    public OAuthV2AccessResponse oauthSlack (OAuthRequest oAuthRequest, UUID frontofficeId) {
        FrontOfficeResponse frontOffice = getFrontOffice(frontofficeId);
        Long memberId = frontOffice.getLaunchedSequenceResponse().getAssignedMember().getId();

        return slackService.oauthMember(oAuthRequest, memberId);
    }


    public SequenceSatisfactionResponse  getSequenceSatisfaction(UUID frontOfficeId){
        var frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        var launchedSequence = frontOffice.getLaunchedSequence();
        if(launchedSequence.getIsScored())
            return SequenceSatisfactionResponse.of(launchedSequence.getSequenceSatisfaction());
        return SequenceSatisfactionResponse.isNone();
    }

    @Transactional
    public SequenceSatisfactionResponse setSequenceSatisfaction(UUID frontOfficeId, SequenceSatisfactionRequest request){
        var frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        var launchedSequence = frontOffice.getLaunchedSequence();
        if(launchedSequence.getIsScored())
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SEQUENCE_ALREADY_SATISFACTION);
        if(!launchedSequence.getCheckSatisfaction())
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SEQUENCE_CAN_NOT_SATISFACTION);
        var satisfaction = SequenceSatisfaction.builder()
                .score(request.getScore())
                .comment(request.getComment())
                .sequence(launchedSequence)
                .member(launchedSequence.getMember())
                .workspace(launchedSequence.getWorkspace())
                .isScored(true)
                .build();
        sequenceSatisfactionRepository.save(satisfaction);
        launchedSequence.setSequenceSatisfaction(satisfaction);
        launchedSequence.setIsScored(true);
        return SequenceSatisfactionResponse.of(satisfaction);
    }
    public Boolean isAuthorized(UUID frontofficeId){
        FrontOfficeResponse frontOffice = getFrontOffice(frontofficeId);
        Long memberId = frontOffice.getLaunchedSequenceResponse().getAssignedMember().getId();

        return slackService.isMemberAuthorized(memberId);
    }

    public PreSignedUrl getAttachmentPresignedUrl(Long launchedModuleId, AttachmentsRequest preSignedRequest){
        var launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
        return attachmentService.getPreSignedUrls(launchedModule, preSignedRequest);
    }

    public AttachmentsResponse getAttachments(Long launchedModuleId){
        var launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
        return attachmentService.getAttachment(launchedModule);
    }
//    public ContentResponse createAttachments(List<AttachmentsRequest> attachmentsRequest, Long launchedModuleId){
//        var launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedModuleId);
//
//        return attachmentService.createAttachments(attachmentsRequest, launchedModule);
//    }

}
