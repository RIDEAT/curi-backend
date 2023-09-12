package com.backend.curi.slack.service;

import com.backend.curi.common.Common;
import com.backend.curi.common.configuration.Constants;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.ChannelRequest;
import com.backend.curi.slack.controller.dto.InviteRequest;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.repository.SlackMemberRepository;
import com.backend.curi.slack.repository.SlackRepository;
import com.backend.curi.slack.repository.entity.SlackInfo;
import com.backend.curi.slack.repository.entity.SlackMemberInfo;
import com.backend.curi.workspace.repository.entity.Role;
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
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.MarkdownTextObject;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static org.apache.http.client.utils.DateUtils.formatDate;

@Service
@RequiredArgsConstructor
public class SlackService {

    private static Logger log = LoggerFactory.getLogger(SlackService.class);

    private final ResourceLoader resourceLoader;

    @Value("${slack.client-id}")
    private String clientId;

    @Value("${slack.client-secret}")
    private String clientSecret;

    @Value("${slack.redirect-uri}")
    private String redirectUri;

    @Value("${slack.member-redirect-uri}")
    private String memberRedirectUri;

    @Value("${slack.rideat-bot-token}")
    private String rideatBotToken;

    @Value("${slack.bot-token}")
    private String botToken;


    @Value("${workplug.app.url}")
    private String appUrl;

    @Value("${rideat.slack.channel.error}")
    private String errorChannel;
    @Value("${rideat.slack.channel.notify}")
    private String notifyChannel;

    private final Common common;

    private final Slack slack = Slack.getInstance();
    private final Constants constants;
    private final SlackRepository slackRepository;
    private final SlackMemberRepository slackMemberRepository;

    public OAuthV2AccessResponse oauthMember(OAuthRequest oAuthRequest, Long memberId) {


        if (!slackMemberRepository.findByMemberId(memberId).isEmpty()) {
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SLACK_OAUTH_ALREADY_EXISTS);
        }

        OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(memberRedirectUri)
                .code(oAuthRequest.getCode())
                .build();

        MethodsClient methods = slack.methods(botToken);
        try {
            OAuthV2AccessResponse response = methods.oauthV2Access(request);

            if (response.isOk()) {
                SlackMemberInfo slackMemberInfo = new SlackMemberInfo();
                slackMemberInfo.setMemberId(memberId);
                slackMemberInfo.setMemberSlackId(response.getAuthedUser().getId());
                slackMemberInfo.setAccessToken(response.getAccessToken());
                slackMemberRepository.save(slackMemberInfo);

                SlackMessageRequest slackMessageRequest = new SlackMessageRequest();
                slackMessageRequest.setTexts("워크플러그 알람이 추가되었습니다."); // needs to change

                sendMessageToMember(slackMessageRequest, memberId);
            }

            return response;
        } catch (CuriException e) {
            log.warn(e.getMessage());

        } catch (SlackApiException e) {
            log.warn(e.getMessage());

        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SLACK_OAUTH_FAILED);
    }

    public OAuthV2AccessResponse oauth(OAuthRequest oAuthRequest) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!slackRepository.findByUserFirebaseId(currentUser.getUserId()).isEmpty()) {
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SLACK_OAUTH_ALREADY_EXISTS);
        }

        try {

            OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .code(oAuthRequest.getCode())
                    .build();

            MethodsClient methods = slack.methods(botToken);
            OAuthV2AccessResponse response = methods.oauthV2Access(request);

            if (response.isOk()) {
                SlackInfo slackInfo = SlackInfo.builder().userFirebaseId(currentUser.getUserId()).accessToken(response.getAccessToken()).userSlackId(response.getAuthedUser().getId()).build();

                slackRepository.save(slackInfo);

                SlackMessageRequest slackMessageRequest = new SlackMessageRequest();
                slackMessageRequest.setTexts("워크플러그 알람이 추가되었습니다.");
                sendMessage(slackMessageRequest);

            } else {
                log.error(response.getError());
            }

            return response;
        } catch (CuriException e) {
            System.out.println(e.getMessage());

            log.warn(e.getMessage());

        } catch (SlackApiException e) {

            System.out.println(e.getMessage());
            log.warn(e.getMessage());

        } catch (Exception e) {
            System.out.println(e.getMessage());

            log.warn(e.getMessage());
        }

        throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SLACK_OAUTH_FAILED);
    }

    public ConversationsCreateResponse createChannel(ChannelRequest channelRequest) throws SlackApiException, IOException {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken(currentUser.getUserId());

        MethodsClient methods = slack.methods(accessToken);
        ConversationsCreateRequest conversationsCreateRequest = ConversationsCreateRequest.builder().name(channelRequest.getChannelName()).isPrivate(true).token(accessToken).build();

        var response = methods.conversationsCreate(conversationsCreateRequest);
        return response;
    }

    public ConversationsInviteResponse invite(InviteRequest inviteRequest) throws SlackApiException, IOException {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken(currentUser.getUserId());

        MethodsClient methods = slack.methods(accessToken);
        List<String> users = new ArrayList<>();
        users.add(inviteRequest.getSlackUserId());

        ConversationsInviteRequest conversationsInviteRequest = ConversationsInviteRequest.builder().channel(inviteRequest.getChannel()).token(accessToken).users(users).build();
        var response = methods.conversationsInvite(conversationsInviteRequest);
        return response;
    }

    public ChatPostMessageResponse sendMessageToRideat(SlackMessageRequest slackMessageRequest) {
        try {
            if (!constants.getENV().equals("cloud")) throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.SLACK_OAUTH_FAILED);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel('#'+notifyChannel) // Use a channel ID `C1234567` is preferable
                    .text(slackMessageRequest.getTexts())
                    .build();


            String accessToken = rideatBotToken;
            MethodsClient methods = slack.methods(accessToken);
            var response = methods.chatPostMessage(request);

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());

        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }
    public ChatPostMessageResponse sendErrorToRideat(SlackMessageRequest slackMessageRequest) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel('#'+errorChannel) // Use a channel ID `C1234567` is preferable
                    .text(slackMessageRequest.getTexts())
                    .build();


            String accessToken = rideatBotToken;
            MethodsClient methods = slack.methods(accessToken);
            var response = methods.chatPostMessage(request);

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());

        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

    public ChatPostMessageResponse sendMessage(SlackMessageRequest slackMessageRequest) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(getSlackId(currentUser.getUserId())) // Use a channel ID `C1234567` is preferable
                    .text(slackMessageRequest.getTexts())
                    .build();


            String accessToken = getAccessToken(currentUser.getUserId());
            MethodsClient methods = slack.methods(accessToken);
            var response = methods.chatPostMessage(request);

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());

        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

    public ChatPostMessageResponse sendMessageToMember(SlackMessageRequest slackMessageRequest, Long memberId) {

        try {
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(slackMemberInfo.getMemberSlackId()) // Use a channel ID `C1234567` is preferable
                    .text(slackMessageRequest.getTexts())
                    .build();


            MethodsClient methods = slack.methods(accessToken);
            var response = methods.chatPostMessage(request);

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());

        } catch (SlackApiException e) {
            log.info(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());

        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;

    }

    public ChatPostMessageResponse sendLaunchedSequenceMessageToMember(LaunchedSequence launchedSequence, FrontOffice frontOffice, Long memberId) {
        try {
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();
            String channelId = slackMemberInfo.getMemberSlackId();
            String frontOfficeUrl = common.getFrontOfficeUrl(frontOffice.getId(), frontOffice.getAccessToken());
            String preText = "`" + launchedSequence.getMember().getName() + "`님에게 할당된 시퀀스가 실행되었습니다.";

            MethodsClient methods = slack.methods(accessToken);

            long currentTimestamp = Instant.now().getEpochSecond();


            Attachment richTextAttachment = Attachment.builder()
                    .mrkdwnIn(List.of("text"))
                    .color("#7b3bed")
                    .pretext("*" + preText + "*").build();




            List<Attachment> attachments = new ArrayList<>();
            attachments.add(richTextAttachment);

            attachments.add(sequenceInfoWithFrontOffice(launchedSequence, frontOfficeUrl));

            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(channelId)
                    .attachments(attachments)
            );


        } catch (CuriException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }


    public ChatPostMessageResponse sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String accessToken = getAccessToken(currentUser.getUserId());
            String channelId = getSlackId(currentUser.getUserId());
            String preText = "`" + launchedWorkflow.getWorkspace().getName() + "` 에서의 워크플로우가 성공적으로 실행되었습니다.";

            return WorkflowLaunchedMessage(launchedWorkflow, accessToken, channelId, preText);
        } catch (CuriException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

    public ChatPostMessageResponse sendWorkflowLaunchedMessageToEmployee(LaunchedWorkflow launchedWorkflow) {
        try {
            Long memberId = launchedWorkflow.getMember().getId();
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();
            String channelId = slackMemberInfo.getMemberSlackId();
            String preText = "`" + launchedWorkflow.getMember().getName() + "`님에게 할당된 워크플로우가 실행되었습니다.";

            return WorkflowLaunchedMessageToMember(launchedWorkflow, accessToken, channelId, preText);
        } catch (CuriException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

    public ChatPostMessageResponse sendWorkflowLaunchedMessageToManagers(LaunchedWorkflow launchedWorkflow, Role role, Member member) {
        try {
            Long memberId = member.getId();
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();
            String channelId = slackMemberInfo.getMemberSlackId();
            String preText = "`" + member.getName() + "`님! `" + launchedWorkflow.getMember().getName() + "`님의 `" + role.getName() + "` 로서 할당된 워크플로우가 있습니다.";
            return WorkflowLaunchedMessageToMember(launchedWorkflow, accessToken, channelId, preText);
        } catch (CuriException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;

    }


    public ChatPostMessageResponse WorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow, String accessToken, String channelId, String preText) {
        try {
            MethodsClient methods = slack.methods(accessToken);
            String workspaceUrl = appUrl + "/workspace/" + launchedWorkflow.getWorkspace().getId();
            String memberUrl = appUrl + "/workspace/" + launchedWorkflow.getWorkspace().getId() + "/member";
            String workflowUrl = workspaceUrl + "/management/" + launchedWorkflow.getWorkflow().getId();

            long currentTimestamp = Instant.now().getEpochSecond();


            Attachment richTextAttachment = Attachment.builder()
                    .mrkdwnIn(List.of("text"))
                    .color("#7b3bed")
                    .pretext("*" + preText + "*")/*
                    .authorName("author_name")
                    .authorLink("http://flickr.com/bobby/")
                    .authorIcon("https://placeimg.com/16/16/people")
                    .title("title")
                    .titleLink("https://api.slack.com/")
                    .text("Optional `text` that appears within the attachment")*/
                    .fields(List.of(
                                    Field.builder()
                                            .title("대상자")
                                            .value("<" + memberUrl + "|`" + launchedWorkflow.getMember().getName() + "`>")
                                            .valueShortEnough(false)
                                            .build(),
                                    Field.builder()
                                            .title("D-Day")
                                            .value(launchedWorkflow.getMember().getStartDate().toString())
                                            .valueShortEnough(true)
                                            .build(),
                                    Field.builder()
                                            .title("워크플로우 이름")
                                            .value("<" + workflowUrl + "|`" + launchedWorkflow.getName() + "`>")
                                            .valueShortEnough(true)
                                            .build()
                            )
                    )
                    //  .footer("<https://app.dev.workplug.team/workspace/330|rideat>" )
                    .footer("<" + workspaceUrl + "|" + launchedWorkflow.getWorkspace().getName() + ">")
                    .footerIcon("https://workplug-logo.s3.ap-northeast-2.amazonaws.com/workplug.png")


//                  .footer("<a href='" + workspaceUrl + "'>" + launchedWorkflow.getWorkspace().getName() + "</a>")
                    .ts(String.valueOf(currentTimestamp))
                    .build();

            Attachment sequenceTitle = Attachment.builder()
                    .mrkdwnIn(List.of("text"))
                    .pretext("*시퀀스 주요정보 (시퀀스는 D-Day 오전 9시에 메일, slack으로 발송됩니다.)*").build();


            List<Attachment> attachments = new ArrayList<>();
            attachments.add(richTextAttachment);
            attachments.add(sequenceTitle);

            for (LaunchedSequence launchedSequence : launchedWorkflow.getLaunchedSequences()) {
                attachments.add(sequenceInfo(launchedSequence));
            }

            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(channelId)
                    .attachments(attachments)
            );

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());


        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

    public ChatPostMessageResponse WorkflowLaunchedMessageToMember(LaunchedWorkflow launchedWorkflow, String accessToken, String channelId, String preText) {
        try {
            MethodsClient methods = slack.methods(accessToken);
            String workspaceUrl = appUrl + "/workspace/" + launchedWorkflow.getWorkspace().getId();
            String memberUrl = appUrl + "/workspace/" + launchedWorkflow.getWorkspace().getId() + "/member";
            String workflowUrl = workspaceUrl + "/management/" + launchedWorkflow.getWorkflow().getId();

            long currentTimestamp = Instant.now().getEpochSecond();


            Attachment richTextAttachment = Attachment.builder()
                    .mrkdwnIn(List.of("text"))
                    .color("#7b3bed")
                    .pretext("*" + preText + "*")/*
                    .authorName("author_name")
                    .authorLink("http://flickr.com/bobby/")
                    .authorIcon("https://placeimg.com/16/16/people")
                    .title("title")
                    .titleLink("https://api.slack.com/")
                    .text("Optional `text` that appears within the attachment")*/
                    .fields(List.of(
                                    Field.builder()
                                            .title("대상자")
                                            .value("`" + launchedWorkflow.getMember().getName() + "`")
                                            .valueShortEnough(false)
                                            .build(),
                                    Field.builder()
                                            .title("D-Day")
                                            .value(launchedWorkflow.getMember().getStartDate().toString())
                                            .valueShortEnough(true)
                                            .build(),
                                    Field.builder()
                                            .title("워크플로우 이름")
                                            .value("`" + launchedWorkflow.getName() + "`")
                                            .valueShortEnough(true)
                                            .build()
                            )
                    )
                    //  .footer("<https://app.dev.workplug.team/workspace/330|rideat>" )
                    .footer(launchedWorkflow.getWorkspace().getName())
                    .footerIcon("https://workplug-logo.s3.ap-northeast-2.amazonaws.com/workplug.png")


//                  .footer("<a href='" + workspaceUrl + "'>" + launchedWorkflow.getWorkspace().getName() + "</a>")
                    .ts(String.valueOf(currentTimestamp))
                    .build();

            Attachment sequenceTitle = Attachment.builder()
                    .mrkdwnIn(List.of("text"))
                    .pretext("*시퀀스 주요정보 (시퀀스는 D-Day 오전 9시에 메일, slack으로 발송됩니다.)*").build();


            List<Attachment> attachments = new ArrayList<>();
            attachments.add(richTextAttachment);
            attachments.add(sequenceTitle);

            for (LaunchedSequence launchedSequence : launchedWorkflow.getLaunchedSequences()) {
                attachments.add(sequenceMemberInfo(launchedSequence));
            }

            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(channelId)
                    .attachments(attachments)
            );

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());


        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }




    Attachment sequenceInfo(LaunchedSequence launchedSequence) throws IOException {
        String workspaceUrl = appUrl + "/workspace/" + launchedSequence.getWorkspace().getId();
        String memberUrl = appUrl + "/workspace/" + launchedSequence.getMember().getWorkspace().getId() + "/member";
        String workflowUrl = workspaceUrl + "/management/" + launchedSequence.getLauchedWorkflow().getWorkflow().getId();
        long currentTimestamp = Instant.now().getEpochSecond();


        return Attachment.builder()
                .mrkdwnIn(List.of("text"))
                .color("#6f6a73")
                /*
                    .authorName("author_name")
                    .authorLink("http://flickr.com/bobby/")
                    .authorIcon("https://placeimg.com/16/16/people")
                    .title("title")
                    .titleLink("https://api.slack.com/")
                    .text("Optional `text` that appears within the attachment")*/
                .fields(List.of(
                                Field.builder()
                                        .title("시퀀스 이름")
                                        .value("<" + workflowUrl + "|`" + launchedSequence.getName() + "`>")
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("대상자")
                                        .value("<" + memberUrl + "|`" + launchedSequence.getMember().getName() + "`>")
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("상태")
                                        .value(launchedSequence.getStatus().toString())
                                        .valueShortEnough(true)
                                        .build(),

                                Field.builder()
                                        .title("D-Day")
                                        .value(launchedSequence.getApplyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .valueShortEnough(true)
                                        .build()

                        )
                )
                //  .footer("<https://app.dev.workplug.team/workspace/330|rideat>" )
                .footer("<" + workspaceUrl + "|" + launchedSequence.getWorkspace().getName() + ">")
                .footerIcon("https://workplug-logo.s3.ap-northeast-2.amazonaws.com/workplug.png")

//                  .footer("<a href='" + workspaceUrl + "'>" + launchedWorkflow.getWorkspace().getName() + "</a>")
                .ts(String.valueOf(currentTimestamp))
                .build();

    }

    Attachment sequenceInfoWithFrontOffice(LaunchedSequence launchedSequence, String frontOfficeUrl) throws IOException {
        String workspaceUrl = appUrl + "/workspace/" + launchedSequence.getWorkspace().getId();
        String memberUrl = appUrl + "/workspace/" + launchedSequence.getMember().getWorkspace().getId() + "/member";
        String workflowUrl = workspaceUrl + "/management/" + launchedSequence.getLauchedWorkflow().getWorkflow().getId();
        long currentTimestamp = Instant.now().getEpochSecond();


        return Attachment.builder()
                .mrkdwnIn(List.of("text"))
                .color("#7b3bed")
                /*
                    .authorName("author_name")
                    .authorLink("http://flickr.com/bobby/")
                    .authorIcon("https://placeimg.com/16/16/people")
                    .title("title")
                    .titleLink("https://api.slack.com/")
                    .text("Optional `text` that appears within the attachment")*/
                .fields(List.of(
                                Field.builder()
                                        .title("시퀀스 이름")
                                        .value("<" + frontOfficeUrl + "|" + launchedSequence.getName() + ">")
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("대상자")
                                        .value( launchedSequence.getMember().getName() )
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("상태")
                                        .value(launchedSequence.getStatus().toString())
                                        .valueShortEnough(true)
                                        .build(),

                                Field.builder()
                                        .title("D-Day")
                                        .value(launchedSequence.getApplyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .valueShortEnough(true)
                                        .build()

                        )
                )
                //  .footer("<https://app.dev.workplug.team/workspace/330|rideat>" )
                .footer(launchedSequence.getWorkspace().getName() )
                .footerIcon("https://workplug-logo.s3.ap-northeast-2.amazonaws.com/workplug.png")

//                  .footer("<a href='" + workspaceUrl + "'>" + launchedWorkflow.getWorkspace().getName() + "</a>")
                .ts(String.valueOf(currentTimestamp))
                .build();

    }


    Attachment sequenceMemberInfo(LaunchedSequence launchedSequence) throws IOException {
        String workspaceUrl = appUrl + "/workspace/" + launchedSequence.getWorkspace().getId();
        String memberUrl = appUrl + "/workspace/" + launchedSequence.getMember().getWorkspace().getId() + "/member";
        String workflowUrl = workspaceUrl + "/management/" + launchedSequence.getLauchedWorkflow().getWorkflow().getId();
        long currentTimestamp = Instant.now().getEpochSecond();


        return Attachment.builder()
                .mrkdwnIn(List.of("text"))
                .color("#6f6a73")
                /*
                    .authorName("author_name")
                    .authorLink("http://flickr.com/bobby/")
                    .authorIcon("https://placeimg.com/16/16/people")
                    .title("title")
                    .titleLink("https://api.slack.com/")
                    .text("Optional `text` that appears within the attachment")*/
                .fields(List.of(
                                Field.builder()
                                        .title("시퀀스 이름")
                                        .value("`" + launchedSequence.getName() + "`")
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("대상자")
                                        .value("`" + launchedSequence.getMember().getName() + "`")
                                        .valueShortEnough(true)
                                        .build(),
                                Field.builder()
                                        .title("상태")
                                        .value(launchedSequence.getStatus().toString())
                                        .valueShortEnough(true)
                                        .build(),

                                Field.builder()
                                        .title("D-Day")
                                        .value(launchedSequence.getApplyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .valueShortEnough(true)
                                        .build()

                        )
                )
                //  .footer("<https://app.dev.workplug.team/workspace/330|rideat>" )
                .footer(launchedSequence.getWorkspace().getName())
                .footerIcon("https://workplug-logo.s3.ap-northeast-2.amazonaws.com/workplug.png")

//                  .footer("<a href='" + workspaceUrl + "'>" + launchedWorkflow.getWorkspace().getName() + "</a>")
                .ts(String.valueOf(currentTimestamp))
                .build();

    }


    public ChatPostMessageResponse sendLaunchedWorkflowDashboard(LaunchedWorkflow launchedWorkflow, String userId) {

        try {
            SlackInfo slackInfo = slackRepository.findByUserFirebaseId(userId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_ADMIN_USER_NOT_AUTHORIZED));

            String accessToken = slackInfo.getAccessToken();
            MethodsClient methods = slack.methods(accessToken);
            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(slackInfo.getUserSlackId())
                    .blocks(buildDashBoardBlocks(launchedWorkflow))
                    .text("현재 대시보드 현황입니다.")
            );

            return response;
        } catch (CuriException e) {
            log.error(e.getMessage());


        } catch (SlackApiException e) {
            log.error(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());


        }
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }


    String getAccessToken(String userId) {
        String accessToken = slackRepository.findByUserFirebaseId(userId).orElseThrow(() -> new CuriException(HttpStatus.FORBIDDEN, ErrorType.SLACK_ACCESS_TOKEN_NOT_EXISTS)).getAccessToken();
        return accessToken;
    }

    protected String getSlackId(String userId) {
        String slackId = slackRepository.findByUserFirebaseId(userId).orElseThrow(() -> new CuriException(HttpStatus.FORBIDDEN, ErrorType.SLACK_ACCESS_TOKEN_NOT_EXISTS)).getUserSlackId();
        return slackId;
    }

    private List<LayoutBlock> buildBlocks(LaunchedWorkflow launchedWorkflow) {
        List<LayoutBlock> blocks = new ArrayList<>();

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text("워크플로우 실행 알림").build())
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

        blocks.add(DividerBlock.builder().build());

        String sequenceHeader = "*시퀀스 정보*\n";

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(sequenceHeader).build())
                .build());


        for (LaunchedSequence sequence : launchedWorkflow.getLaunchedSequences()) {
            String sequenceDetails = "*시퀀스이름: * " + sequence.getName() + "\n" +
                    "*시작일: * " + sequence.getApplyDate().toString() + "\n" +
                    "*대상자: * " + sequence.getMember().getName();

            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder().text(sequenceDetails).build())
                    .build());

        }

        return blocks;
    }

    private List<LayoutBlock> buildEmployeeBlocks(LaunchedWorkflow launchedWorkflow) {
        List<LayoutBlock> blocks = new ArrayList<>();

        Member employee = launchedWorkflow.getMember();
        String message = "*안녕하세요. " + employee.getName() + "님의 원할한 온보딩을 응원합니다!*\n";

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(message).build())
                .build());

        blocks.add(DividerBlock.builder().build());

        String employeeInfoHeader = "*신규 입사자 정보*\n" + "이름 : " + employee.getName() + "\n" + "부서 : " + employee.getDepartment() + "\n" + "입사일자: " + employee.getStartDate();

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(employeeInfoHeader).build())
                .build());

        blocks.add(DividerBlock.builder().build());

        String sequenceHeader = "*" + employee.getName() + "님이 참여할 활동*\n";

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(sequenceHeader).build())
                .build());

        for (LaunchedSequence sequence : launchedWorkflow.getLaunchedSequences()) {
            if (sequence.getMember().equals(employee)) {
                String sequenceDetails = "*시퀀스이름: * " + sequence.getName() + "\n" +
                        "*시작일: * " + sequence.getApplyDate().toString();

                blocks.add(SectionBlock.builder()
                        .text(MarkdownTextObject.builder().text(sequenceDetails).build())
                        .build());
            }
        }

        return blocks;
    }



    private List<LayoutBlock> buildDashBoardBlocks(LaunchedWorkflow launchedWorkflow) {
        List<LayoutBlock> blocks = new ArrayList<>();

        // Welcome message
        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(":sparkles: *런치된 워크플로우 진행 현황입니다.* :sparkles:").build())
                .build());
        blocks.add(DividerBlock.builder().build());

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

        // Sequence Progress
        StringBuilder sequenceProgress = new StringBuilder(":chart_with_upwards_trend: *시퀀스 진행 상황:*\n\n");

        for (LaunchedSequence launchedSequence : launchedWorkflow.getLaunchedSequences()) {
            sequenceProgress.append(":small_orange_diamond: *").append(launchedSequence.getName()).append(":* ").append(launchedSequence.getStatus()).append("\n");
        }

        // Create a rich text section block for sequence progress
        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(sequenceProgress.toString()).build())
                .blockId("sequence-progress")
                .build());

        // Sequence Details and Toggle Buttons
        for (LaunchedSequence launchedSequence : launchedWorkflow.getLaunchedSequences()) {
            StringBuilder sequenceInfo = new StringBuilder();

            // Build sequence information
            sequenceInfo.append("*시퀀스 이름:* ").append(launchedSequence.getName()).append("\n");
            sequenceInfo.append("*시퀀스 상태:* ").append(launchedSequence.getStatus()).append("\n");
            sequenceInfo.append("*적용 일자:* ").append(launchedSequence.getApplyDate()).append("\n");


        }

        // Calculate sequence progress
        int totalSequences = launchedWorkflow.getLaunchedSequences().size();
        int completedSequences = 0;
        int inProgressOrCompletedSequences = 0;

        for (LaunchedSequence launchedSequence : launchedWorkflow.getLaunchedSequences()) {
            if (launchedSequence.getStatus().equals(LaunchedStatus.COMPLETED)) {
                completedSequences++;
                inProgressOrCompletedSequences++;
            } else if (launchedSequence.getStatus().equals(LaunchedStatus.IN_PROGRESS) || launchedSequence.getStatus().equals(LaunchedStatus.COMPLETED)) {
                inProgressOrCompletedSequences++;
            }
        }

        String progressText;
        if (inProgressOrCompletedSequences == 0) {
            progressText = ":x: 활성화된 시퀀스가 없습니다.";
        } else {
            int progressPercentage = (completedSequences * 100) / inProgressOrCompletedSequences;
            progressText = ":chart_with_upwards_trend: *전체 진행률:* " + progressPercentage + "%";
        }

        // Create a rich text section block for progress
        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(progressText).build())
                .blockId("progress")
                .build());

        return blocks;
    }


    public Boolean isAuthorized() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !slackRepository.findByUserFirebaseId(currentUser.getUserId()).isEmpty();
    }

    public Boolean isMemberAuthorized(Long memberId) {
        return !slackMemberRepository.findByMemberId(memberId).isEmpty();
    }

    public void deleteOauth() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SlackInfo slackInfo = slackRepository.findByUserFirebaseId(currentUser.getUserId()).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SLACK_ADMIN_USER_NOT_AUTHORIZED));
        slackRepository.delete(slackInfo);
    }
}
