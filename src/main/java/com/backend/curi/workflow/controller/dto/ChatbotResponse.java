package com.backend.curi.workflow.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatbotResponse {
    boolean ok;
    String message;
}
