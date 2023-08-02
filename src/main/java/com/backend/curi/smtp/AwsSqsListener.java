package com.backend.curi.smtp;

import com.amazonaws.services.simpleemail.model.Message;
import com.backend.curi.smtp.dto.SequenceMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AwsSqsListener {
    private final ObjectMapper objectMapper;

    @SqsListener("${cloud.aws.sqs.queue.name}")
    private void receiveMessage(@Headers Map<String, String> header, @Payload String message) throws JsonProcessingException {
        var msg = objectMapper.readValue(message, SequenceMessageDto.class);
    }
}
