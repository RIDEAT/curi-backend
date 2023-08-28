package com.backend.curi.slack.controller;

import com.backend.curi.slack.controller.dto.ChannelRequest;
import com.backend.curi.slack.controller.dto.InviteRequest;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
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



    private final SlackService slackService;

    @PostMapping("/oauth")
    public ResponseEntity<OAuthV2AccessResponse> oauth(@Valid @RequestBody OAuthRequest oAuthRequest) throws SlackApiException, IOException {
        var response = slackService.oauth(oAuthRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/channel")
    public ResponseEntity<ConversationsCreateResponse> createChannel(@Valid @RequestBody ChannelRequest channelRequest) throws SlackApiException, IOException {
        var response = slackService.createChannel(channelRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invite")
    public ResponseEntity<ConversationsInviteResponse> invite(@Valid @RequestBody InviteRequest inviteRequest) throws SlackApiException, IOException {
        var response = slackService.invite(inviteRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/message")
    public ResponseEntity<ChatPostMessageResponse> sendMessage (@Valid @RequestBody SlackMessageRequest slackMessageRequest) throws SlackApiException, IOException {
        var response = slackService.sendMessage(slackMessageRequest);
        return ResponseEntity.ok(response);
    }


}
