package com.backend.curi.message.service;


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
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    private final SlackService slackService;
    private final AwsSMTPService awsSMTPService;
    private final NotificationService notificationService;


    public void sendLaunchedSequenceMessage(String memberTo, FrontOffice frontOffice, LaunchedSequence launchedSequence){
        log.info("런치드 시퀀스 전송");
        awsSMTPService.sendLaunchedSequenceMessageToMember(launchedSequence, frontOffice, memberTo);
        slackService.sendLaunchedSequenceMessageToMember(launchedSequence, frontOffice, launchedSequence.getMember().getId());
    }

    public void sendWorkflowLaunchedMessage (LaunchedWorkflow launchedWorkflow, Map<Role, Member> memberMap){
        // send to Admin user
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("send workflow launch alarm to admin");
        slackService.sendWorkflowLaunchedMessage(launchedWorkflow);
        awsSMTPService.sendWorkflowLaunchedMessage(launchedWorkflow, currentUser.getUserId());
        notificationService.createNotification(launchedWorkflow.getWorkspace().getId(), "워크플로우 실행!", "새로운 워크플로우가 실행되었습니다.");

        log.info ("send workflow launch alarm to employee");
        Member employee = launchedWorkflow.getMember();
        slackService.sendWorkflowLaunchedMessageToEmployee(launchedWorkflow);
        awsSMTPService.sendWorkflowLaunchedMessageToEmployee(launchedWorkflow, employee);

        log.info ("send workflow launch alarm to related managers");
        for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
            Role role = entry.getKey();
            Member member = entry.getValue();
            // with following code, we can prevent self-assigned situation (신규입사자 to 신규입사자)
            if (role.getName().equals("신규입사자")) continue;
            slackService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, role, member);
            awsSMTPService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, member);

        }

    }


}
