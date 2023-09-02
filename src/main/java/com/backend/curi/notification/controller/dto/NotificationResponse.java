package com.backend.curi.notification.controller.dto;

import com.backend.curi.notification.repository.entity.Notifications;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Object contents;
    private Long workspaceId;

    public static NotificationResponse of(Notifications notifications) {
        return NotificationResponse.builder()
                .contents(notifications.getContent())
                .workspaceId(notifications.getWorkspaceId())
                .build();
    }
}
