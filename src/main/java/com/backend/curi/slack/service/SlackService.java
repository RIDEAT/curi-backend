package com.backend.curi.slack.service;

import com.backend.curi.common.configuration.LoggingAspect;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedModule;
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
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.MarkdownTextObject;

import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ButtonElement;
import com.slack.api.model.block.element.ImageElement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static org.apache.http.client.utils.DateUtils.formatDate;

@Service
@RequiredArgsConstructor
public class SlackService {

    private static Logger log = LoggerFactory.getLogger(SlackService.class);

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

    public OAuthV2AccessResponse oauthMember(OAuthRequest oAuthRequest, Long memberId) {


        if (!slackMemberRepository.findByMemberId(memberId).isEmpty()) {
            var response = new OAuthV2AccessResponse();
            response.setOk(false);
            response.setError("이미 인증받은 멤버입니다.");
            return response;
        }

        OAuthV2AccessRequest request = OAuthV2AccessRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri + "/member")
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
                slackMessageRequest.setTexts("온버드 알람이 추가되었습니다."); // needs to change

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

        OAuthV2AccessResponse oAuthV2AccessResponse = new OAuthV2AccessResponse();
        oAuthV2AccessResponse.setOk(false);
        return oAuthV2AccessResponse;
    }

    public OAuthV2AccessResponse oauth(OAuthRequest oAuthRequest) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!slackRepository.findByUserFirebaseId(currentUser.getUserId()).isEmpty()) {
                var response = new OAuthV2AccessResponse();
                response.setOk(false);
                response.setError("이미 인증받은 어드민입니다.");
                return response;
            }

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
                slackMessageRequest.setTexts("온버드 알람이 추가되었습니다.");
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

        OAuthV2AccessResponse oAuthV2AccessResponse = new OAuthV2AccessResponse();
        oAuthV2AccessResponse.setOk(false);
        return oAuthV2AccessResponse;
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

            List<LayoutBlock> blocks = new ArrayList<>();

            // Add a section block with rich text formatting
            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                            .text("🚀 *오늘 할당된 시퀀스가 도착했습니다!* 🎉")
                            .build())
                    .build());

            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                            .text("안녕하세요, " + launchedSequence.getMember().getName() + " 님! 🌼")
                            .build())
                    .build());

            // Add a divider block for visual separation
            blocks.add(DividerBlock.builder().build());

            // Add a section block with detailed information
            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                            .text("오늘 할당된 시퀀스에 대한 상세 내용은 아래에서 확인하실 수 있습니다.")
                            .build())
                    .build());

            // Add a link to the Front Office URL
            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                            .text("🔗 [프론트 오피스에서 시퀀스 확인하기](" + getFrontOfficeUrl(frontOffice.getId(), frontOffice.getAccessToken()) + ")")
                            .build())
                    .build());

            // Add a closing message
            blocks.add(SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                            .text("시퀀스 내용을 확인하시고 필요한 작업을 진행해 주시기 바랍니다.\n더 많은 정보와 도움이 필요하신 경우, 온버드 웹사이트 또는 지원팀에 문의해 주세요.\n감사합니다. 😊")
                            .build())
                    .build());

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(slackMemberInfo.getMemberSlackId()) // Use a channel ID `C1234567` is preferable
                    .blocks(blocks)
                    .text("오늘 할당된 시퀀스가 도착했습니다! 🚀")
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


    public ChatPostMessageResponse sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String accessToken = getAccessToken(currentUser.getUserId());
            MethodsClient methods = slack.methods(accessToken);
            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(getSlackId(currentUser.getUserId()))
                    .blocks(buildBlocks(launchedWorkflow))
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

    public ChatPostMessageResponse sendWorkflowLaunchedMessageToEmployee(LaunchedWorkflow launchedWorkflow) {
        try {
            Long memberId = launchedWorkflow.getMember().getId();
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();
            MethodsClient methods = slack.methods(accessToken);
            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(slackMemberInfo.getMemberSlackId())
                    .blocks(buildEmployeeBlocks(launchedWorkflow))
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

    public ChatPostMessageResponse sendWorkflowLaunchedMessageToManagers(LaunchedWorkflow launchedWorkflow, Role role, Member member) {

        try {
            Long memberId = member.getId();
            SlackMemberInfo slackMemberInfo = slackMemberRepository.findByMemberId(memberId).orElseThrow(() -> new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.SLACK_MEMBER_NOT_AUTHORIZED));
            String accessToken = slackMemberInfo.getAccessToken();
            MethodsClient methods = slack.methods(accessToken);
            ChatPostMessageResponse response = methods.chatPostMessage(req -> req
                    .channel(slackMemberInfo.getMemberSlackId())
                    .blocks(buildManagerBlocks(launchedWorkflow, role, member))
                    .text("default")
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
                String sequenceDetails = "*활동명: * " + sequence.getName() + "\n" +
                        "*시작일: * " + sequence.getApplyDate().toString();

                blocks.add(SectionBlock.builder()
                        .text(MarkdownTextObject.builder().text(sequenceDetails).build())
                        .build());
            }
        }
        return blocks;
    }

    private List<LayoutBlock> buildManagerBlocks(LaunchedWorkflow launchedWorkflow, Role role, Member manager) {
        List<LayoutBlock> blocks = new ArrayList<>();
        Member employee = launchedWorkflow.getMember();


        String message = "*안녕하세요. " + manager.getName() + "님 ! 당신은 " +
                employee.getName() + "님의 " + role.getName() + " 입니다. *\n" +
                employee.getName() + "님의 성공적인 온보딩을 함께 해주세요!\n";

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(message).build())
                .build());

        blocks.add(DividerBlock.builder().build());

        String employeeInfoHeader = "*신규 입사자 정보*\n" + "이름 : " + employee.getName() + "\n" + "부서 : " + employee.getDepartment() + "\n" + "입사일자: " + employee.getStartDate();

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(employeeInfoHeader).build())
                .build());

        blocks.add(DividerBlock.builder().build());

        String sequenceHeader = "*" + manager.getName() + "님이 " + employee.getName() + "님의 " + role.getName() + "으로서 참여할 활동*\n";

        blocks.add(SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(sequenceHeader).build())
                .build());

        for (LaunchedSequence sequence : launchedWorkflow.getLaunchedSequences()) {
            if (sequence.getMember().equals(manager)) {
                blocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text(sequence.getName()).build()).build());
                blocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text(sequence.getApplyDate().toString()).build()).build());

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


    private String getFrontOfficeUrl(UUID id, UUID accessToken) {
        return "https://view.dev.onbird.team/" + id + "?token=" + accessToken;
    }

    public Boolean isAuthorized() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !slackRepository.findByUserFirebaseId(currentUser.getUserId()).isEmpty();
    }

    public void deleteOauth() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SlackInfo slackInfo = slackRepository.findByUserFirebaseId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.SLACK_ADMIN_USER_NOT_AUTHORIZED));
        slackRepository.delete(slackInfo);
    }
}
