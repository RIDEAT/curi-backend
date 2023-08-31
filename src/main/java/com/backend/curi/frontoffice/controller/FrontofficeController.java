package com.backend.curi.frontoffice.controller;

import com.backend.curi.frontoffice.controller.dto.FrontOfficeResponse;
import com.backend.curi.frontoffice.controller.dto.LaunchedModuleWithContent;
import com.backend.curi.frontoffice.controller.dto.SequenceSatisfactionRequest;
import com.backend.curi.frontoffice.controller.dto.SequenceSatisfactionResponse;
import com.backend.curi.frontoffice.service.FrontOfficeService;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.workflow.repository.entity.SequenceSatisfaction;
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

    @PostMapping("/{frontOfficeId}/sequence-satisfaction")
    public ResponseEntity<SequenceSatisfactionResponse> setSatisfaction(@PathVariable UUID frontOfficeId, @Valid @RequestBody SequenceSatisfactionRequest sequenceSatisfactionRequest){
        var response =  frontofficeService.setSequenceSatisfaction(frontOfficeId, sequenceSatisfactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{frontOfficeId}/oauth")
    public ResponseEntity<OAuthV2AccessResponse> oauthMember(@PathVariable UUID frontOfficeId, @Valid @RequestBody OAuthRequest oAuthRequest) throws SlackApiException, IOException {
        FrontOfficeResponse frontofficeResponse =  frontofficeService.getFrontOffice(frontOfficeId);
        var response = frontofficeService.oauthSlack(oAuthRequest, frontOfficeId);
        return ResponseEntity.ok(response);
    }


}
