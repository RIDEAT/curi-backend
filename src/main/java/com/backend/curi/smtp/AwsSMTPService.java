package com.backend.curi.smtp;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.backend.curi.common.Common;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.service.LaunchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AwsSMTPService {
    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private static Logger log = LoggerFactory.getLogger(AwsSMTPService.class);

    private final Common common;

    private final TemplateEngine templateEngine;
    @Value("${workplug.app.url}")
    private String appUrl;

    @Value("${workplug.view.url}")
    private String viewUrl;


    private final String from;

    public AwsSMTPService(
            AmazonSimpleEmailService amazonSimpleEmailService,
            Common common,
            @Value("${cloud.aws.ses.from}") String from,
            TemplateEngine templateEngine
    ) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.common = common;
        this.from = "no-reply@" + from;
        this.templateEngine = templateEngine;
    }

    public void send(String subject, String content, String... to) {

        var sendEmailRequest = createSendEmailRequest(subject, content, to);

        amazonSimpleEmailService.sendEmail(sendEmailRequest);
    }

    public void sendThymeleaf(String subject, String content, CurrentUser currentUser, Member member, LaunchedWorkflow launchedWorkflow, String... to) {

        var sendEmailRequest = createSendThymeleafRequest(subject, content, currentUser, member, launchedWorkflow, to);

        amazonSimpleEmailService.sendEmail(sendEmailRequest);
    }

    public void sendSequenceThymeleaf(String subject, String content, Member member, FrontOffice frontOffice, LaunchedSequence launchedSequence, String... to) {

        var sendEmailRequest = createSendSequnceThymeleafRequest(subject, content, member,frontOffice, launchedSequence, to);

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

    public SendEmailRequest createSendThymeleafRequest(String subject, String templateName, CurrentUser currentUser, Member member, LaunchedWorkflow launchedWorkflow, String... to) {
        // Thymeleaf를 사용하여 HTML 템플릿을 렌더링
        Context context = new Context();
        //context.setVariable("name", "JiSeung"); // 사용자 이름 또는 데이터를 설정
        context.setVariable("user", currentUser);
        context.setVariable("workflow", launchedWorkflow);
        context.setVariable("member", member);
        context.setVariable("viewUrl", viewUrl);

        String emailContent = templateEngine.process(templateName, context);

        // SendEmailRequest 생성
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withSource(from)
                .withMessage(new Message()
                        .withSubject(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(subject))
                        .withBody(new Body().withHtml(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(emailContent)))
                );

        return sendEmailRequest;
    }
    public SendEmailRequest createSendSequnceThymeleafRequest(String subject, String templateName, Member member,FrontOffice frontOffice, LaunchedSequence launchedSequence, String... to) {
        // Thymeleaf를 사용하여 HTML 템플릿을 렌더링
        Context context = new Context();
        context.setVariable("sequence", launchedSequence);
        context.setVariable("member", member);
        context.setVariable("frontUrl" , common.getFrontOfficeUrl(frontOffice.getId(),frontOffice.getAccessToken()));

        String emailContent = templateEngine.process(templateName, context);

        // SendEmailRequest 생성
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withSource(from)
                .withMessage(new Message()
                        .withSubject(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(subject))
                        .withBody(new Body().withHtml(new Content().withCharset(StandardCharsets.UTF_8.name()).withData(emailContent)))
                );

        return sendEmailRequest;
    }


    public void sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow, CurrentUser currentUser, Member member) {
        String userEmail = currentUser.getUserId();
        String userName = currentUser.getName();
        sendThymeleaf("워크플로우 실행 알림", "launch-workflow-to-admin", currentUser,member, launchedWorkflow, userEmail);
      //  send("워크플로우 실행 알림", launchWorkflowMailTemplate(userName, launchedWorkflow), userEmail);

    }

    private String getSequenceMessage(LaunchedWorkflow launchedWorkflow) {
        String message = "";
        List<LaunchedSequence> launchedSequences = launchedWorkflow.getLaunchedSequences();
        if (!launchedSequences.isEmpty()) {
            message += "<p><strong>시퀀스 목록:</strong></p>";
            message += "<ul>";

            for (LaunchedSequence sequence : launchedSequences) {
                message += "<li><strong>시퀀스 이름: </strong> " + sequence.getName() + "</li>";
                message += "<li><strong>D-Day: </strong> " + sequence.getApplyDate() + "</li>";
                message += "<li><strong>대상자: </strong> " + sequence.getMember().getName() + "</li>";
                message += "<br/>";
            }

            message += "</ul>";


        }
        return message;
    }

    public void sendWorkflowLaunchedMessageToEmployee(LaunchedWorkflow launchedWorkflow,CurrentUser currentUser, Member employee) {
        String employeeEmail = employee.getEmail();
        sendThymeleaf("워크플로우 할당 알림", "launch-workflow-to-member", currentUser, employee, launchedWorkflow, employeeEmail);
    }

    public void sendWorkflowLaunchedMessageToManagers(LaunchedWorkflow launchedWorkflow, CurrentUser currentUser, Member member) {
        String managerEmail = member.getEmail();
        sendThymeleaf("워크플로우 할당 알림", "launch-workflow-to-member", currentUser, member, launchedWorkflow, managerEmail);
    }



    public void sendLaunchedSequenceMessageToMember(LaunchedSequence launchedSequence, FrontOffice frontOffice, Member member) {

        String name = launchedSequence.getMember().getName();
        sendSequenceThymeleaf("오늘 할당된 시퀀스가 있습니다!", "sequence-to-member", member,  frontOffice, launchedSequence, member.getEmail());
    }


}
