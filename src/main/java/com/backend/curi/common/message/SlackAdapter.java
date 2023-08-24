package com.backend.curi.common.message;

import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import lombok.RequiredArgsConstructor;

class SlackAdapter implements MessageService {
    private final SlackService slackService;

    public SlackAdapter(SlackService slackService) {
        this.slackService = slackService;
    }

    @Override
    public void sendMessage(String message) {
     //   slackService.sendMessage(new SlackMessageRequest(message));
    }
}
