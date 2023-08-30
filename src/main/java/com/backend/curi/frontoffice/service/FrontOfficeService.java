package com.backend.curi.frontoffice.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.controller.dto.FrontOfficeResponse;
import com.backend.curi.frontoffice.controller.dto.LaunchedModuleWithContent;
import com.backend.curi.frontoffice.repository.FrontOfficeRepository;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.slack.controller.dto.OAuthRequest;

import com.backend.curi.slack.service.SlackService;

import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.service.ContentService;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FrontOfficeService {

    private final SlackService slackService;
    private final FrontOfficeRepository frontOfficeRepository;
    private final LaunchedModuleService launchedModuleService;
    private final ContentService contentService;

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

    public void checkAuth(UUID frontOfficeId, UUID accessToken) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        if (!frontOffice.getAccessToken().equals(accessToken)) throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.FRONTOFFICE_UNAUTHORIZED);

    }

    public void createFrontOffice(LaunchedSequence launchedSequence) {
        FrontOffice frontOffice = new FrontOffice();
        frontOffice.setLaunchedSequence(launchedSequence);
        frontOffice.setAccessToken(UUID.randomUUID());
        frontOfficeRepository.save(frontOffice);

    }

    public FrontOffice getFrontOfficeByLaunchedSequenceId(Long launchedSequenceId) {
        return frontOfficeRepository.findByLaunchedSequenceId(launchedSequenceId).orElseThrow(()-> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
    }

    public OAuthV2AccessResponse oauthSlack (OAuthRequest oAuthRequest, UUID frontofficeId) {
        FrontOfficeResponse frontOffice = getFrontOffice(frontofficeId);
        Long memberId = frontOffice.getLaunchedSequenceResponse().getAssignedMember().getId();

        return slackService.oauthMember(oAuthRequest, memberId);
    }


}
