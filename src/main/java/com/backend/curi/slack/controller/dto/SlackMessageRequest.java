package com.backend.curi.slack.controller.dto;

import com.slack.api.methods.SlackApiRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SlackMessageRequest implements SlackApiRequest {
    String token;
    String channelId;
    String text;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token){this.token = token;}


}
