package com.backend.curi.slack.controller;

import com.backend.curi.member.controller.dto.ManagerRequest;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slack")
public class SlackController {

    private final Slack slack = Slack.getInstance();



    @PostMapping("/oauth")
    public ResponseEntity<OAuthV2AccessResponse> oauth(@Valid @RequestBody OAuthRequest oAuthRequest) throws SlackApiException, IOException {


        OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                .clientId(oAuthRequest.getClientId())
                .clientSecret(oAuthRequest.getClientSecret())
                .redirectUri(oAuthRequest.getRedirectUri())
                .code(oAuthRequest.getCode())
                .build();


        MethodsClient methods = slack.methods(oAuthRequest.getToken());
        OAuthV2AccessResponse response = methods.oauthV2Access(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("message")
    public ResponseEntity<ChatPostMessageResponse> sendMessage (@RequestBody SlackMessageRequest slackMessageRequest) throws SlackApiException, IOException {
        MethodsClient methods = slack.methods(slackMessageRequest.getToken());
        // Build a request object
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackMessageRequest.getChannelId()) // Use a channel ID `C1234567` is preferable
                .text(slackMessageRequest.getText())
                .build();



        ChatPostMessageResponse response = methods.chatPostMessage(request);
        return ResponseEntity.ok(response);
    }

}
