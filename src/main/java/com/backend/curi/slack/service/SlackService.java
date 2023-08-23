package com.backend.curi.slack.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.ChannelRequest;
import com.backend.curi.slack.controller.dto.InviteRequest;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.repository.SlackMemberRepository;
import com.backend.curi.slack.repository.SlackRepository;
import com.backend.curi.slack.repository.entity.SlackInfo;
import com.backend.curi.slack.repository.entity.SlackMemberInfo;
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
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.MarkdownTextObject;

import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ImageElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static org.apache.http.client.utils.DateUtils.formatDate;

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
    private final SlackMemberRepository slackMemberRepository;

    public OAuthV2AccessResponse oauthMember (OAuthRequest oAuthRequest, Long memberId) throws SlackApiException, IOException {


        if (!slackMemberRepository.findByMemberId(memberId).isEmpty()) {
            var response = new OAuthV2AccessResponse();
            response.setOk(false);
            response.setError("이미 인증받은 멤버입니다.");
            return response;
        }

        OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri+"member")
                .code(oAuthRequest.getCode())
                .build();

        MethodsClient methods = slack.methods(botToken);
        OAuthV2AccessResponse response = methods.oauthV2Access(request);

        if (response.isOk()) {
            SlackMemberInfo slackMemberInfo = new SlackMemberInfo();
            slackMemberInfo.setMemberId(memberId);
            slackMemberInfo.setMemberSlackId(response.getAuthedUser().getId());
            slackMemberInfo.setAccessToken(response.getAccessToken());
            slackMemberRepository.save(slackMemberInfo);

            SlackMessageRequest slackMessageRequest = new SlackMessageRequest();
            slackMessageRequest.setTexts("큐리알람이 추가되었습니다."); // needs to change

            sendMessageToMember(slackMessageRequest, memberId);
        }

        return response;
    }
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
            SlackInfo slackInfo = SlackInfo.builder().userFirebaseId(currentUser.getUserId()).accessToken(response.getAccessToken()).userSlackId(response.getAuthedUser().getId()).build();

            slackRepository.save(slackInfo);

            ChannelRequest channelRequest = new ChannelRequest("curi-alarm");
            var res = createChannel(channelRequest);

            InviteRequest inviteRequest = new InviteRequest(res.getChannel().getId(),response.getAuthedUser().getId());
            invite(inviteRequest);

            slackInfo.setChannelId(res.getChannel().getId());

            slackRepository.save(slackInfo);

            SlackMessageRequest slackMessageRequest = new SlackMessageRequest();
            slackMessageRequest.setTexts("큐리알람이 추가되었습니다.");
            sendMessage(slackMessageRequest);

        }

        return response;
    }

    public ConversationsCreateResponse createChannel(ChannelRequest channelRequest) throws SlackApiException, IOException {
        String accessToken = getAccessToken();

        MethodsClient methods = slack.methods(accessToken);
        ConversationsCreateRequest conversationsCreateRequest = ConversationsCreateRequest.builder().name(channelRequest.getChannelName()).isPrivate(true).token(accessToken).build();

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

    public ChatPostMessageResponse sendMessage (SlackMessageRequest slackMessageRequest){

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(getAlarmChannelId()) // Use a channel ID `C1234567` is preferable
                .blocksAsString(slackMessageRequest.getBlocksAsString())
                .text("default")
                .build();


        String accessToken = getAccessToken();
        MethodsClient methods = slack.methods(accessToken);


        try {
            var response = methods.chatPostMessage(request);

            //ChatPostMessageResponse response = methods.chatPostMessage(request);
            //System.out.println(response.getError());
            return response;
        } catch (SlackApiException e){

        } catch (Exception e){

        }

        throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);

    }

    public ChatPostMessageResponse sendMessageToMember (SlackMessageRequest slackMessageRequest, Long memberId){
        SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(()->new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
        String accessToken = slackMemberInfo.getAccessToken();

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackMemberInfo.getMemberSlackId()) // Use a channel ID `C1234567` is preferable
                .text(slackMessageRequest.getTexts())
                .build();


        MethodsClient methods = slack.methods(accessToken);
        try {
            var response = methods.chatPostMessage(request);

            //ChatPostMessageResponse response = methods.chatPostMessage(request);
            //System.out.println(response.getError());
            return response;
        } catch (SlackApiException e){

        } catch (Exception e){

        }

        throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);

    }


    public ChatPostMessageResponse sendWorkflowLaunchedMessage (LaunchedWorkflow launchedWorkflow){


        String accessToken = getAccessToken();
        MethodsClient methods = slack.methods(accessToken);


        try {
            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(getAlarmChannelId())
                    .blocks(buildBlocks(launchedWorkflow))

            );

            return response;
        } catch (SlackApiException e){

        } catch (Exception e){

        }

        throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);

    }

    String getAccessToken (){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = slackRepository.findByUserFirebaseId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.FORBIDDEN, ErrorType.SLACK_ACCESS_TOKEN_NOT_EXISTS)).getAccessToken();
        return accessToken;
    }

    protected String getAlarmChannelId(){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String channelId = slackRepository.findByUserFirebaseId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.FORBIDDEN, ErrorType.SLACK_ACCESS_TOKEN_NOT_EXISTS)).getChannelId();
        return channelId;
    }

    private List<LayoutBlock> buildBlocks(LaunchedWorkflow launchedWorkflow) {
        List<LayoutBlock> blocks = new ArrayList<>();

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(":tada: *워크플로우 런치 알림* :tada:").build())
                .build());
        // Workflow Details
        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder()
                        .text(
                                "*신규 입사자:* " + String.join(", ", launchedWorkflow.getMember().getName()) + "\n" +
                                        "*워크플로우 이름:* " + launchedWorkflow.getName() + "\n" +
                                        "*입사 일자:* " + launchedWorkflow.getKeyDate()
                        )
                        .build())
                .build());

        //blocks.add(DividerBlock.builder().build());

        // Sample Order List (You can replace this with your actual order details)
        /*  List<LaunchedSequence> sequences = launchedWorkflow.getLaunchedSequences(); // Replace with your logic to retrieve order items
        for (LaunchedSequence sequence: sequences) {
            blocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text(sequence.getName()).build()).build());

        }*/

        return blocks;
    }



}
