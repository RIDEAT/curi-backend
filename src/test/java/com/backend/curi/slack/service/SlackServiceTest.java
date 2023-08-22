package com.backend.curi.slack;

import com.backend.curi.slack.repository.SlackRepository;
import com.backend.curi.slack.service.SlackService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class SlackServiceTest {
    SlackRepository slackRepository = mock(SlackRepository.class);

    SlackService slackService = new SlackService(slackRepository);

    @Test
    public void testtest(){
        when(slackService)
        slackService.sendMessage
    }
}
