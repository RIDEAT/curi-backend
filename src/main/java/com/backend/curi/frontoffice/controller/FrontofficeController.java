package com.backend.curi.frontoffice.controller;

import com.backend.curi.frontoffice.controller.dto.FrontofficeResponse;
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

    @GetMapping("/{frontofficeId}")
    public ResponseEntity<FrontofficeResponse> getLaunchedsequence(@PathVariable UUID frontofficeId){
        FrontofficeResponse frontofficeResponse =  frontofficeService.getFrontOffice(frontofficeId);
        return ResponseEntity.ok(frontofficeResponse);
    }

    @PostMapping("/{frontofficeId}/oauth")
    public ResponseEntity<OAuthV2AccessResponse> oauthMember(@PathVariable UUID frontofficeId, @Valid @RequestBody OAuthRequest oAuthRequest) throws SlackApiException, IOException {
        var response = frontofficeService.oauthSlack(oAuthRequest, frontofficeId);
        return ResponseEntity.ok(response);
    }


}
