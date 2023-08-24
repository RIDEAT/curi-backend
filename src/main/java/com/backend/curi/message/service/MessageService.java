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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    private final SlackService slackService;
    private final AwsSMTPService awsSMTPService;


    public void sendLaunchedSequenceMessage(String memberTo, FrontOffice frontOffice, LaunchedSequence launchedSequence){
        String name = launchedSequence.getMember().getName();
        String emailContent = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #0084ff;'>🌟 오늘 할당된 시퀀스가 있습니다! 🌟</h2>"
                + "<p>안녕하세요, " + name + " 님!</p>"
                + "<p>오늘 할당된 시퀀스를 안내드립니다. 아래 링크를 통해 시퀀스 상세 내용을 확인하실 수 있습니다.</p>"
                + "<p><strong>프론트 오피스 URL:</strong> <a href='" + getFrontOfficeUrl(frontOffice.getId(), frontOffice.getAccessToken()) + "'>시퀀스 보기</a></p>"
                + "<p>시퀀스 내용을 확인하시고 필요한 작업을 진행해 주시기 바랍니다.</p>"
                + "<p>더 많은 정보와 도움이 필요하신 경우, 온버드 웹사이트 또는 지원팀에 문의해 주세요.</p>"
                + "<p>감사합니다.</p>"
                + "</body></html>";
        log.info("런치드 시퀀스 메일 전송 to : {}, frontOfficeId: {}",memberTo, frontOffice.getId() );
        awsSMTPService.send("오늘 할당된 시퀀스가 있습니다!", emailContent, memberTo);

        log.info("슬랙 전송 to : {}",memberTo );

        slackService.sendLaunchedSequenceMessageToMember(launchedSequence, frontOffice, launchedSequence.getMember().getId());

    }

    private String getFrontOfficeUrl(UUID id, UUID accessToken) {
        return "https://view.dev.onbird.team/" + id +"?token=" + accessToken;
    }

    public void sendWorkflowLaunchedMessage (LaunchedWorkflow launchedWorkflow, Map<Role, Member> memberMap){
        // send to Admin user
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("send workflow launch alarm to admin");

        slackService.sendWorkflowLaunchedMessage(launchedWorkflow);


        awsSMTPService.send("워크플로우가 런치되었습니다!", "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                "<ui>"+
                "<li><strong>워크플로우 제목:</strong>" + launchedWorkflow.getName() + "</li>\n" +
                "<li><strong>시행 날짜:</strong>" + launchedWorkflow.getLaunchedSequences().get(0).getApplyDate() +"</li>\n" +
                "<li><strong>신규입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>\n"+
                "</ul>" +
                "<p>더 많은 정보와 상세 내용은  <a href=\"https://app.dev.onbird.team/\">온버드 웹사이트</a>에서 확인하세요.</p>\n" , currentUser.getUserEmail());


        log.info ("send workflow launch alarm to employee");
        String employeeEmail = launchedWorkflow.getMember().getEmail();
        awsSMTPService.send("신규입사자 "+launchedWorkflow.getMember().getName() +"님께 할당된 워크플로우입니다.", "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                "<ui>"+
                "<li><strong>워크플로우 제목:</strong>" + launchedWorkflow.getName() + "</li>\n" +
                "<li><strong>시행 날짜:</strong>" + launchedWorkflow.getLaunchedSequences().get(0).getApplyDate() +"</li>\n" +
                "<li><strong>신규입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>\n"+
                "</ul>" +
                "<p>더 많은 정보와 상세 내용은  <a href=\"https://app.dev.onbird.team/\">온버드 웹사이트</a>에서 확인하세요.</p>\n" ,  employeeEmail);



        log.info ("send workflow launch alarm to related managers");
        for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
            Role role = entry.getKey();
            Member member = entry.getValue();
            slackService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, role, member);
            awsSMTPService.send("워크플로우가 런치되었습니다!", "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                    "<ui>"+
                    "<li><strong>워크플로우 제목:</strong>" + launchedWorkflow.getName() + "</li>\n" +
                    "<li><strong>시행 날짜:</strong>" + launchedWorkflow.getLaunchedSequences().get(0).getApplyDate() +"</li>\n" +
                    "<li><strong>신규입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>\n"+
                    "</ul>" +
                    "<p>더 많은 정보와 상세 내용은  <a href=\"https://app.dev.onbird.team/\">온버드 웹사이트</a>에서 확인하세요.</p>\n" , member.getEmail());        }

    }
}
