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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${workplug.app.url}")
    private String appUrl;

    private final String from;

    public AwsSMTPService(
            AmazonSimpleEmailService amazonSimpleEmailService,
            Common common,
            @Value("${cloud.aws.ses.from}") String from
    ) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.common = common;
        this.from = "no-reply@" + from;
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

    public void sendWorkflowLaunchedMessage(LaunchedWorkflow launchedWorkflow, CurrentUser currentUser) {
        String userEmail = currentUser.getUserId();
        String userName = currentUser.getName();
        send("워크플로우 실행 알림", launchWorkflowMailTemplate(userName, launchedWorkflow), userEmail);

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

    public void sendWorkflowLaunchedMessageToEmployee(LaunchedWorkflow launchedWorkflow, Member employee) {
        String employeeEmail = employee.getEmail();
        send("워크플로우 할당 알림", launchWorkflowMailTemplate(employee.getName(), launchedWorkflow), employeeEmail);
    }

    public void sendWorkflowLaunchedMessageToManagers(LaunchedWorkflow launchedWorkflow, Member member) {
        send("워크플로우 실행 알림", launchWorkflowMailTemplate(member.getName(), launchedWorkflow), member.getEmail());
    }

    public void sendLaunchedSequenceMessageToMember(LaunchedSequence launchedSequence, FrontOffice frontOffice, String memberTo) {

        String name = launchedSequence.getMember().getName();
        String emailContent = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #0084ff;'>🌟 오늘 할당된 시퀀스가 있습니다! 🌟</h2>"
                + "<p>안녕하세요, " + name + " 님! </행p>"
                + "<p>오늘 할당된 시퀀스를 안내드립니다. 아래 링크를 통해 시퀀스 상세 내용을 확인하실 수 있습니다.</p>"
                + "<p><strong>URL:</strong> <a href='" + common.getFrontOfficeUrl(frontOffice.getId(), frontOffice.getAccessToken()) + "'>시퀀스 보기</a></p>"
                + "<p>시퀀스 내용을 확인하시고 필요한 작업을 진행해 주시기 바랍니다.</p>"
                + "<p>더 많은 정보와 도움이 필요하신 경우, <a href=\"https://app.workplug.team/\" style=\"color: #007bff;\">워크플러그 웹사이트</a>에 문의해 주세요.</p>"
                + "<p>감사합니다.</p>"
                + "</body></html>";

        send("오늘 할당된 시퀀스가 있습니다!", emailContent, memberTo);
    }

    private String launchWorkflowMailTemplate(String userName, LaunchedWorkflow launchedWorkflow){
        return
        "<!doctype html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <title>Simple Transactional Email</title>\n" +
                "    <style>\n" +
                "      /* -------------------------------------\n" +
                "          GLOBAL RESETS\n" +
                "      ------------------------------------- */\n" +
                "      \n" +
                "      /*All the styling goes here*/\n" +
                "      \n" +
                "      img {\n" +
                "        border: none;\n" +
                "        -ms-interpolation-mode: bicubic;\n" +
                "        max-width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      body {\n" +
                "        color: #000000;\n"+
                "        background-color: #f6f6f6;\n" +
                "        font-family: sans-serif;\n" +
                "        -webkit-font-smoothing: antialiased;\n" +
                "        font-size: 14px;\n" +
                "        line-height: 1.4;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "        -ms-text-size-adjust: 100%;\n" +
                "        -webkit-text-size-adjust: 100%; \n" +
                "      }\n" +
                "\n" +
                "      table {\n" +
                "        border-collapse: separate;\n" +
                "        mso-table-lspace: 0pt;\n" +
                "        mso-table-rspace: 0pt;\n" +
                "        width: 100%; }\n" +
                "        table td {\n" +
                "          font-family: sans-serif;\n" +
                "          font-size: 14px;\n" +
                "          vertical-align: top; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          BODY & CONTAINER\n" +
                "      ------------------------------------- */\n" +
                "\n" +
                "      .body {\n" +
                "        background-color: #f6f6f6;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      /* Set a max-width, and make it display as block so it will automatically stretch to that width, but will also shrink down on a phone or something */\n" +
                "      .container {\n" +
                "        display: block;\n" +
                "        margin: 0 auto !important;\n" +
                "        /* makes it centered */\n" +
                "        max-width: 580px;\n" +
                "        padding: 10px;\n" +
                "        width: 580px; \n" +
                "      }\n" +
                "\n" +
                "      /* This should also be a block element, so that it will fill 100% of the .container */\n" +
                "      .content {\n" +
                "        box-sizing: border-box;\n" +
                "        display: block;\n" +
                "        margin: 0 auto;\n" +
                "        max-width: 580px;\n" +
                "        padding: 10px; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          HEADER, FOOTER, MAIN\n" +
                "      ------------------------------------- */\n" +
                "      .main {\n" +
                "        background: #ffffff;\n" +
                "        border-radius: 3px;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      .wrapper {\n" +
                "        box-sizing: border-box;\n" +
                "        padding: 20px; \n" +
                "      }\n" +
                "\n" +
                "      .content-block {\n" +
                "        padding-bottom: 10px;\n" +
                "        padding-top: 10px;\n" +
                "      }\n" +
                "\n" +
                "      .footer {\n" +
                "        clear: both;\n" +
                "        margin-top: 10px;\n" +
                "        text-align: center;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "        .footer td,\n" +
                "        .footer p,\n" +
                "        .footer span,\n" +
                "        .footer a {\n" +
                "          color: #999999;\n" +
                "          font-size: 12px;\n" +
                "          text-align: center; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          TYPOGRAPHY\n" +
                "      ------------------------------------- */\n" +
                "      h1,\n" +
                "      h2,\n" +
                "      h3,\n" +
                "      h4 {\n" +
                "        color: #000000;\n" +
                "        font-family: sans-serif;\n" +
                "        font-weight: 400;\n" +
                "        line-height: 1.4;\n" +
                "        margin: 0;\n" +
                "        margin-bottom: 30px; \n" +
                "      }\n" +
                "\n" +
                "      h1 {\n" +
                "        font-size: 35px;\n" +
                "        font-weight: 300;\n" +
                "        text-align: center;\n" +
                "        text-transform: capitalize; \n" +
                "      }\n" +
                "\n" +
                "      p,\n" +
                "      ul,\n" +
                "      ol {\n" +
                "        font-family: sans-serif;\n" +
                "        font-size: 14px;\n" +
                "        font-weight: normal;\n" +
                "        margin: 0;\n" +
                "        margin-bottom: 15px; \n" +
                "      }\n" +
                "        p li,\n" +
                "        ul li,\n" +
                "        ol li {\n" +
                "          list-style-position: inside;\n" +
                "          margin-left: 5px; \n" +
                "      }\n" +
                "\n" +
                "      a {\n" +
                "        color: #3498db;\n" +
                "        text-decoration: underline; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          BUTTONS\n" +
                "      ------------------------------------- */\n" +
                "      .btn {\n" +
                "        box-sizing: border-box;\n" +
                "        width: 100%; }\n" +
                "        .btn > tbody > tr > td {\n" +
                "          padding-bottom: 15px; }\n" +
                "        .btn table {\n" +
                "          width: auto; \n" +
                "      }\n" +
                "        .btn table td {\n" +
                "          background-color: #ffffff;\n" +
                "          border-radius: 5px;\n" +
                "          text-align: center; \n" +
                "      }\n" +
                "        .btn a {\n" +
                "          background-color: #ffffff;\n" +
                "          border: solid 1px #3498db;\n" +
                "          border-radius: 5px;\n" +
                "          box-sizing: border-box;\n" +
                "          color: #3498db;\n" +
                "          cursor: pointer;\n" +
                "          display: inline-block;\n" +
                "          font-size: 14px;\n" +
                "          font-weight: bold;\n" +
                "          margin: 0;\n" +
                "          padding: 12px 25px;\n" +
                "          text-decoration: none;\n" +
                "          text-transform: capitalize; \n" +
                "      }\n" +
                "\n" +
                "      .btn-primary table td {\n" +
                "        background-color: #3498db; \n" +
                "      }\n" +
                "\n" +
                "      .btn-primary a {\n" +
                "        background-color: #3498db;\n" +
                "        border-color: #3498db;\n" +
                "        color: #ffffff; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          OTHER STYLES THAT MIGHT BE USEFUL\n" +
                "      ------------------------------------- */\n" +
                "      .last {\n" +
                "        margin-bottom: 0; \n" +
                "      }\n" +
                "\n" +
                "      .first {\n" +
                "        margin-top: 0; \n" +
                "      }\n" +
                "\n" +
                "      .align-center {\n" +
                "        text-align: center; \n" +
                "      }\n" +
                "\n" +
                "      .align-right {\n" +
                "        text-align: right; \n" +
                "      }\n" +
                "\n" +
                "      .align-left {\n" +
                "        text-align: left; \n" +
                "      }\n" +
                "\n" +
                "      .clear {\n" +
                "        clear: both; \n" +
                "      }\n" +
                "\n" +
                "      .mt0 {\n" +
                "        margin-top: 0; \n" +
                "      }\n" +
                "\n" +
                "      .mb0 {\n" +
                "        margin-bottom: 0; \n" +
                "      }\n" +
                "\n" +
                "      .preheader {\n" +
                "        color: transparent;\n" +
                "        display: none;\n" +
                "        height: 0;\n" +
                "        max-height: 0;\n" +
                "        max-width: 0;\n" +
                "        opacity: 0;\n" +
                "        overflow: hidden;\n" +
                "        mso-hide: all;\n" +
                "        visibility: hidden;\n" +
                "        width: 0; \n" +
                "      }\n" +
                "\n" +
                "      .powered-by a {\n" +
                "        text-decoration: none; \n" +
                "      }\n" +
                "\n" +
                "      hr {\n" +
                "        border: 0;\n" +
                "        border-bottom: 1px solid #f6f6f6;\n" +
                "        margin: 20px 0; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          RESPONSIVE AND MOBILE FRIENDLY STYLES\n" +
                "      ------------------------------------- */\n" +
                "      @media only screen and (max-width: 620px) {\n" +
                "        table.body h1 {\n" +
                "          font-size: 28px !important;\n" +
                "          margin-bottom: 10px !important; \n" +
                "        }\n" +
                "        table.body p,\n" +
                "        table.body ul,\n" +
                "        table.body ol,\n" +
                "        table.body td,\n" +
                "        table.body span,\n" +
                "        table.body a {\n" +
                "          font-size: 16px !important; \n" +
                "        }\n" +
                "        table.body .wrapper,\n" +
                "        table.body .article {\n" +
                "          padding: 10px !important; \n" +
                "        }\n" +
                "        table.body .content {\n" +
                "          padding: 0 !important; \n" +
                "        }\n" +
                "        table.body .container {\n" +
                "          padding: 0 !important;\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .main {\n" +
                "          border-left-width: 0 !important;\n" +
                "          border-radius: 0 !important;\n" +
                "          border-right-width: 0 !important; \n" +
                "        }\n" +
                "        table.body .btn table {\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .btn a {\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .img-responsive {\n" +
                "          height: auto !important;\n" +
                "          max-width: 100% !important;\n" +
                "          width: auto !important; \n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          PRESERVE THESE STYLES IN THE HEAD\n" +
                "      ------------------------------------- */\n" +
                "      @media all {\n" +
                "        .ExternalClass {\n" +
                "          width: 100%; \n" +
                "        }\n" +
                "        .ExternalClass,\n" +
                "        .ExternalClass p,\n" +
                "        .ExternalClass span,\n" +
                "        .ExternalClass font,\n" +
                "        .ExternalClass td,\n" +
                "        .ExternalClass div {\n" +
                "          line-height: 100%; \n" +
                "        }\n" +
                "        .apple-link a {\n" +
                "          color: inherit !important;\n" +
                "          font-family: inherit !important;\n" +
                "          font-size: inherit !important;\n" +
                "          font-weight: inherit !important;\n" +
                "          line-height: inherit !important;\n" +
                "          text-decoration: none !important; \n" +
                "        }\n" +
                "        #MessageViewBody a {\n" +
                "          color: inherit;\n" +
                "          text-decoration: none;\n" +
                "          font-size: inherit;\n" +
                "          font-family: inherit;\n" +
                "          font-weight: inherit;\n" +
                "          line-height: inherit;\n" +
                "        }\n" +
                "        .btn-primary table td:hover {\n" +
                "          background-color: #34495e !important; \n" +
                "        }\n" +
                "        .btn-primary a:hover {\n" +
                "          background-color: #34495e !important;\n" +
                "          border-color: #34495e !important; \n" +
                "        } \n" +
                "      }\n" +
                "\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\">\n" +
                "      <tr>\n" +
                "        <td>&nbsp;</td>\n" +
                "        <td class=\"container\">\n" +
                "          <div class=\"content\">\n" +
                "\n" +

                "            <!-- START CENTERED WHITE CONTAINER -->\n" +
                "            <table role=\"presentation\" class=\"main\">\n" +
                "\n" +

                "              <!-- START MAIN CONTENT AREA -->\n" +
                "              <tr>\n" +
                "                <td class=\"wrapper\">\n" +

                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                    <tr>\n" +
                "                      <td>\n" +

                "                        <p>안녕하세요! " + userName + "님</p>\n" +
                "                        <p>" + launchedWorkflow.getName() + " 워크플로우가 실행되었습니다.</p>\n" +
                "                        <p>아래는 상세 내용입니다:</p>\n" +
                getSequenceMessage(launchedWorkflow) +

                "                        <p>수행할 시퀀스는 시퀀스 별 D-Day 당일 오전 9시에 메일로 전송됩니다.</p>\n" +
                "                        <p>감사합니다.</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "\n" +
                "            <!-- END MAIN CONTENT AREA -->\n" +
                "            </table>\n" +
                "            <!-- END CENTERED WHITE CONTAINER -->\n" +
                "\n" +
                "            <!-- START FOOTER -->\n" +
                "            <div class=\"footer\">\n" +
                "              <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                <tr>\n" +
                "                  <td class=\"content-block\">\n" +
                "                    <span class=\"apple-link\">워크플러그</span>\n" +
                "<br>"+
                "                    <span class=\"apple-link\">대표자 강민혁</span>\n" +
                "<br>"+
                "                    <span class=\"apple-link\">010-3303-6681</span>\n" +
                "<br>"+
                "                    <span class=\"apple-link\">rkdalsgur032@gmail.com\n</span>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td class=\"content-block powered-by\">\n" +
                "                    Powered by <a href=\"http://htmlemail.io\">HTMLemail</a>.\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </div>\n" +
                "            <!-- END FOOTER -->\n" +
                "\n" +
                "          </div>\n" +
                "        </td>\n" +
                "        <td>&nbsp;</td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +


                "</html>";

    }
}
