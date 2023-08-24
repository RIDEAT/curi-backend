package com.backend.curi.frontoffice.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.controller.dto.FrontofficeResponse;
import com.backend.curi.frontoffice.repository.FrontOfficeRepository;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.repository.entity.SlackMemberInfo;
import com.backend.curi.slack.service.SlackService;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FrontOfficeService {

    private final SlackService slackService;
    private final FrontOfficeRepository frontOfficeRepository;

    public FrontofficeResponse getFrontOffice(UUID frontOfficeId) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        return FrontofficeResponse.of(frontOffice);

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
        FrontofficeResponse frontOffice = getFrontOffice(frontofficeId);
        Long memberId = frontOffice.getLaunchedSequenceResponse().getAssignedMember().getId();

        return slackService.oauthMember(oAuthRequest, memberId);
    }


}
