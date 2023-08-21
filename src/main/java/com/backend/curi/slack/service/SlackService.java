package com.backend.curi.slack.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.ChannelRequest;
import com.backend.curi.slack.controller.dto.InviteRequest;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.repository.SlackRepository;
import com.backend.curi.slack.repository.entity.SlackToken;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsCreateRequest;
import com.slack.api.methods.request.conversations.ConversationsInviteRequest;
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
import com.slack.api.methods.response.oauth.OAuthV2AccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${slack.client-id}")
    private String clientId;

    @Value("${slack.client-secret}")
    private String clientSecret;

    @Value("${slack.redirect-uri}")
    private String redirectUri;

    @Value("${slack.bot-token}")
    private String botToken;

    private final Slack slack = Slack.getInstance();
    private final SlackRepository slackRepository;


    public OAuthV2AccessResponse oauth (OAuthRequest oAuthRequest) throws SlackApiException, IOException {

        OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .code(oAuthRequest.getCode())
                .build();

        MethodsClient methods = slack.methods(botToken);
        OAuthV2AccessResponse response = methods.oauthV2Access(request);

        if (response.isOk()) {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            SlackToken slackToken = SlackToken.builder().userFirebaseId(currentUser.getUserId()).accessToken(response.getAccessToken()).userSlackId(response.getAuthedUser().getId()).build();
            slackRepository.save(slackToken);
        }

        return response;
    }

    public ConversationsCreateResponse createChannel(ChannelRequest channelRequest) throws SlackApiException, IOException {
        String accessToken = getAccessToken();

        MethodsClient methods = slack.methods(accessToken);
        ConversationsCreateRequest conversationsCreateRequest = ConversationsCreateRequest.builder().name(channelRequest.getChannelName()).isPrivate(false).token(accessToken).build();

        var response = methods.conversationsCreate(conversationsCreateRequest);
        return response;
    }

    public ConversationsInviteResponse invite(InviteRequest inviteRequest) throws SlackApiException, IOException {
        String accessToken = getAccessToken();

        MethodsClient methods = slack.methods(accessToken);
        List<String> users = new ArrayList<>();
        users.add(inviteRequest.getSlackUserId());

        ConversationsInviteRequest conversationsInviteRequest = ConversationsInviteRequest.builder().channel(inviteRequest.getChannel()).token(accessToken).users(users).build();
        var response = methods.conversationsInvite(conversationsInviteRequest);
        return response;
    }

    public ChatPostMessageResponse sendMessage (SlackMessageRequest slackMessageRequest) throws SlackApiException, IOException {

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackMessageRequest.getChannelId()) // Use a channel ID `C1234567` is preferable
                .text(slackMessageRequest.getText())
                .build();

        String accessToken = getAccessToken();
        MethodsClient methods = slack.methods(accessToken);
        ChatPostMessageResponse response = methods.chatPostMessage(request);
        return response;
    }


    private String getAccessToken (){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = slackRepository.findByUserFirebaseId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.FORBIDDEN, ErrorType.SLACK_ACCESS_TOKEN_NOT_EXISTS)).getAccessToken();
        return accessToken;
    }





}
