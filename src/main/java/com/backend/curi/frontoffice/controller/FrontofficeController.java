package com.backend.curi.frontoffice.controller;

import com.backend.curi.frontoffice.controller.dto.AuthorizedResponse;
import com.backend.curi.frontoffice.controller.dto.FrontOfficeResponse;
import com.backend.curi.frontoffice.controller.dto.LaunchedModuleWithContent;
import com.backend.curi.frontoffice.service.FrontOfficeService;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/front-offices")
@RequiredArgsConstructor
public class FrontofficeController {

    private final FrontOfficeService frontofficeService;

    @GetMapping("/{frontOfficeId}")
    public ResponseEntity<FrontOfficeResponse> getFrontOffice(@PathVariable UUID frontOfficeId){
        FrontOfficeResponse frontofficeResponse =  frontofficeService.getFrontOffice(frontOfficeId);
        return ResponseEntity.ok(frontofficeResponse);
    }

    @GetMapping("/{frontOfficeId}/launched-modules/{launchedModuleId}")
    public ResponseEntity<LaunchedModuleWithContent> getLaunchedModuleWithContent(@PathVariable UUID frontOfficeId, @PathVariable Long launchedModuleId){
        LaunchedModuleWithContent module =  frontofficeService.getLaunchedModuleWithContent(launchedModuleId);
        return ResponseEntity.ok(module);
    }

    @PostMapping("/{frontOfficeId}/launched-modules/{launchedModuleId}/complete")
    public ResponseEntity<LaunchedModuleWithContent> completeModule(@PathVariable UUID frontOfficeId, @PathVariable Long launchedModuleId){
        LaunchedModuleWithContent module =  frontofficeService.completeLaunchedModuleWithContent(launchedModuleId);
        return ResponseEntity.ok(module);
    }



    @PostMapping("/{frontOfficeId}/oauth")
    public ResponseEntity<OAuthV2AccessResponse> oauthMember(@PathVariable UUID frontOfficeId, @Valid @RequestBody OAuthRequest oAuthRequest) throws SlackApiException, IOException {
        FrontOfficeResponse frontofficeResponse =  frontofficeService.getFrontOffice(frontOfficeId);
        var response = frontofficeService.oauthSlack(oAuthRequest, frontOfficeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{frontOfficeId}/isAuthorized")
    public ResponseEntity<AuthorizedResponse> isAuthorized(@PathVariable UUID frontOfficeId) throws SlackApiException, IOException {
        Boolean isAuthorized = frontofficeService.isAuthorized(frontOfficeId);
        AuthorizedResponse response = new AuthorizedResponse(isAuthorized);
        return ResponseEntity.ok(response);
    }

}
