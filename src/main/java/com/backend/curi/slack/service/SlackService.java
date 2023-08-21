package com.backend.curi.slack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackService {

    @Value("slack.client-id")
    private String client_id;

    @Value("slack.client-secret")
    private String client_secret;

    @Value("slack.redirect-url")
    private String redirect_url;

    @Value("slack.bot-token")
    private String bot_token;
}
