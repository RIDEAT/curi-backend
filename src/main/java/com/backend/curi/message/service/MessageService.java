package com.backend.curi.message.service;


import com.backend.curi.common.configuration.Constants;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.notification.repository.entity.Notifications;
import com.backend.curi.notification.service.NotificationService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.smtp.AwsSMTPService;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    private final SlackService slackService;
    private final AwsSMTPService awsSMTPService;
    private final NotificationService notificationService;
    private final Constants constants;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");


    public void sendLaunchedSequenceMessage(Member memberTo, FrontOffice frontOffice, LaunchedSequence launchedSequence) {
        slackService.sendMessageToRideat(new SlackMessageRequest("시퀀스 메일 발송" +launchedSequence.getMember().getName() + "님에게 할당된 시퀀스(" + launchedSequence.getName() + ") 메일이 발송되었습니다."));
        log.info("런치드 시퀀스 전송");
        awsSMTPService.sendLaunchedSequenceMessageToMember(launchedSequence, frontOffice, memberTo);
        slackService.sendLaunchedSequenceMessageToMember(launchedSequence, frontOffice, launchedSequence.getMember().getId());
        notificationService.createNotification(launchedSequence.getWorkspace().getId(), "시퀀스 메일 발송", launchedSequence.getMember().getName() + "님에게 할당된 시퀀스(" + launchedSequence.getName() + ") 메일이 발송되었습니다.");

    }

    public void sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow, Map<Role, Member> memberMap) {
        // send to Admin user
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        slackService.sendMessageToRideat(new SlackMessageRequest("워크플로우 실행 메일 발송" + launchedWorkflow.getMember().getName() + "님에게 할당된 워크플로우(" + launchedWorkflow.getName() + ")가 실행 예정 상태입니다. D-Day (D-0) : " + launchedWorkflow.getKeyDate().format(formatter)));

        log.info("send workflow launch alarm to admin");
        slackService.sendWorkflowLaunchedMessage(launchedWorkflow);
        awsSMTPService.sendWorkflowLaunchedMessage(launchedWorkflow, currentUser, launchedWorkflow.getMember());

        if (constants.getENV().equals("cloud"))
            notificationService.createNotification(launchedWorkflow.getWorkspace().getId(), "워크플로우 실행 예정", launchedWorkflow.getMember().getName() + "님에게 할당된 워크플로우(" + launchedWorkflow.getName() + ")가 실행 예정 상태입니다. D-Day (D-0) : " + launchedWorkflow.getKeyDate().format(formatter));

        log.info("send workflow launch alarm to employee");
        Member employee = launchedWorkflow.getMember();
        slackService.sendWorkflowLaunchedMessageToEmployee(launchedWorkflow);
        awsSMTPService.sendWorkflowLaunchedMessageToEmployee(launchedWorkflow, currentUser, employee);

        log.info("send workflow launch alarm to related managers");
        for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
            Role role = entry.getKey();
            Member member = entry.getValue();
            // with following code, we can prevent self-assigned situation (신규입사자 to 신규입사자)
            if (launchedWorkflow.getMember().equals(member)) continue;

            slackService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, role, member);
            awsSMTPService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, currentUser, member);

        }

    }


    public void sendWorkspaceCreateMessage(Workspace savedWorkspace, CurrentUser currentUser) {
        String userName = (currentUser.getName() != null) ? currentUser.getName() : "워크플러그 유저";
        if (constants.getENV().equals("cloud")) notificationService.createNotification(savedWorkspace.getId(), "워크스페이스 생성 완료", userName +"님 동료와 연결되는 새로운 방식 '워크플러그'를 사용해주셔서 감사합니다! 먼저 좌측바의 상단에 있는 워크플로우 탭을 눌러 예시 워크플로우를 확인해보세요!");
    }
}