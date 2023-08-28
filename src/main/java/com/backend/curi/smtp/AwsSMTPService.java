package com.backend.curi.smtp;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workflow.service.LaunchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class AwsSMTPService {
    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private static Logger log = LoggerFactory.getLogger(AwsSMTPService.class);


    private final String from;

    public AwsSMTPService(
            AmazonSimpleEmailService amazonSimpleEmailService,
            @Value("${cloud.aws.ses.from}") String from
    ) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.from = "no-reply@curiboard.com";
    }

    public void send(String subject, String content, String... to) {

        var sendEmailRequest = createSendEmailRequest(subject, content, to);

        amazonSimpleEmailService.sendEmail(sendEmailRequest);
    }


    private SendEmailRequest createSendEmailRequest(String subject, String content, String... to) {
        return new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withSource(from)
                .withMessage(new Message()
                        .withSubject(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(subject))
                        .withBody(new Body().withHtml(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(content)))
                );
    }

    public void sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow, String userEmail) {
        String message = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; padding: 20px; border-radius: 10px;\">" +
                "<h2 style=\"color: #007bff;\">🚀 새로운 워크플로우가 시작되었습니다! 🚀</h2>" +
                "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                "<ul style=\"list-style-type: none; padding-left: 0;\">" +
                "<li><strong>신규 입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>" +
                "<li><strong>워크플로우 이름:</strong> " + launchedWorkflow.getName() + "</li>" +
                "<li><strong>입사 일자:</strong> " + launchedWorkflow.getMember().getStartDate() + "</li>" +
                "</ul>" +
                "<p>더 많은 정보와 상세 내용은 <a href=\"https://app.dev.onbird.team/\" style=\"color: #007bff;\">온버드 웹사이트</a>에서 확인하세요.</p>" +
                "<p>감사합니다.</p>" +
                "</div>";

        send("워크플로우 런치 알림", message, userEmail);

    }

    public void sendWorkflowLaunchedMessageToEmployee(LaunchedWorkflow launchedWorkflow, String employeeEmail) {
        String message = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; padding: 20px; border-radius: 10px;\">" +
                "<h2 style=\"color: #007bff;\">🚀 새로운 워크플로우가 시작되었습니다! 🚀</h2>" +
                "<p>안녕하세요, " + launchedWorkflow.getMember().getName() + " 님! </p>" +
                "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                "<ul style=\"list-style-type: none; padding-left: 0;\">" +
                "<li><strong>신규 입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>" +
                "<li><strong>워크플로우 이름:</strong> " + launchedWorkflow.getName() + "</li>" +
                "<li><strong>입사 일자:</strong> " + launchedWorkflow.getMember().getStartDate() + "</li>" +
                "</ul>" +
                "<p>더 많은 정보와 상세 내용은 <a href=\"https://app.dev.onbird.team/\" style=\"color: #007bff;\">온버드 웹사이트</a>에서 확인하세요.</p>" +
                "<p>시작하신 워크플로우가 성공적으로 진행되길 바랍니다!</p>" +
                "<p>감사합니다.</p>" +
                "</div>";

        send("워크플로우 런치 알림", message, employeeEmail);

    }

    public void sendWorkflowLaunchedMessageToManagers(LaunchedWorkflow launchedWorkflow, Member member) {
        String message = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; padding: 20px; border-radius: 10px;\">" +
                "<h2 style=\"color: #007bff;\">🚀 새로운 워크플로우가 시작되었습니다! 🚀</h2>" +
                "<p>안녕하세요, " + member.getName() + " 님! </p>" +
                "<p>새로운 워크플로우가 시작되었습니다. 아래는 상세 내용입니다:</p>" +
                "<ul style=\"list-style-type: none; padding-left: 0;\">" +
                "<li><strong>신규 입사자:</strong> " + launchedWorkflow.getMember().getName() + "</li>" +
                "<li><strong>워크플로우 이름:</strong> " + launchedWorkflow.getName() + "</li>" +
                "<li><strong>입사 일자:</strong> " + launchedWorkflow.getMember().getStartDate() + "</li>" +
                "</ul>" +
                "<p>더 많은 정보와 상세 내용은 <a href=\"https://app.dev.onbird.team/\" style=\"color: #007bff;\">온버드 웹사이트</a>에서 확인하세요.</p>" +
                "<p>"+ launchedWorkflow.getMember().getName() +"님의 성공적인 온보딩을 함께 해주세요!</p>" +
                "<p>감사합니다.</p>" +
                "</div>";

        send("워크플로우 런치 알림", message, member.getEmail());
    }

    public void sendLaunchedSequenceMessageToMember(LaunchedSequence launchedSequence, FrontOffice frontOffice, String memberTo) {

        String name = launchedSequence.getMember().getName();
        String emailContent = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #0084ff;'>🌟 오늘 할당된 시퀀스가 있습니다! 🌟</h2>"
                + "<p>안녕하세요, " + name + " 님! </p>"
                + "<p>오늘 할당된 시퀀스를 안내드립니다. 아래 링크를 통해 시퀀스 상세 내용을 확인하실 수 있습니다.</p>"
                + "<p><strong>URL:</strong> <a href='" + getFrontOfficeUrl(frontOffice.getId(), frontOffice.getAccessToken()) + "'>시퀀스 보기</a></p>"
                + "<p>시퀀스 내용을 확인하시고 필요한 작업을 진행해 주시기 바랍니다.</p>"
                + "<p>더 많은 정보와 도움이 필요하신 경우, 온버드 웹사이트 또는 지원팀에 문의해 주세요.</p>"
                + "<p>감사합니다.</p>"
                + "</body></html>";

        send("오늘 할당된 시퀀스가 있습니다!", emailContent, memberTo);
    }

    private String getFrontOfficeUrl(UUID id, UUID accessToken) {
        return "https://view.dev.onbird.team/" + id +"?token=" + accessToken;
    }
}
