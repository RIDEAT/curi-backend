package com.backend.curi.message.service;


import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.smtp.AwsSMTPService;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workspace.repository.entity.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    private final SlackService slackService;
    private final AwsSMTPService awsSMTPService;


    public void sendLaunchedSequenceMessage(String memberTo, FrontOffice frontOffice, LaunchedSequence launchedSequence){
        log.info("런치드 시퀀스 메일 전송 to : {}, frontOfficeId: {}",memberTo, frontOffice.getId() );
        awsSMTPService.send("할당된 시퀀스입니다. 아래에 접속하시면 됩니다.", "프론트 오피스 url: https://view.dev.onbird.team/" + frontOffice.getId() +"?token= " + frontOffice.getAccessToken(), memberTo);
        //여기서 슬랙도 보내고 슬랙 연동이 안된경우 슬랙 연동 url 도 보내고
        log.info("슬랙 전송 to : {}",memberTo );
        slackService.sendMessageToMember(new SlackMessageRequest("프론트 오피스로 접속하세요 url, password"), launchedSequence.getMember().getId());

    }

    public void sendWorkflowLaunchedMessage (LaunchedWorkflow launchedWorkflow, Map<Role, Member> memberMap){
        // send to Admin user
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("send workflow launch alarm to admin");

        slackService.sendWorkflowLaunchedMessage(launchedWorkflow);


        awsSMTPService.send("워크플로우가 런치되었습니다.", "워크플로우가 런치되었습니다.", currentUser.getUserEmail());

        log.info ("send workflow launch alarm to employee");
        String employeeEmail = launchedWorkflow.getMember().getEmail();
        awsSMTPService.send("workflow is launched", "this is test", employeeEmail);

        log.info ("send workflow launch alarm to related managers");
        for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
            Role role = entry.getKey();
            Member member = entry.getValue();
            slackService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, role, member);
            awsSMTPService.send("workflow is launched", "this is test", member.getEmail());
        }

    }
}
