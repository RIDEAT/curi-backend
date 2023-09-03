package com.backend.curi.notification.controller.dto;

import com.backend.curi.notification.repository.entity.Notifications;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private ObjectId id;
    private Object contents;
    private Long workspaceId;

    public static NotificationResponse of(Notifications notifications) {
        return NotificationResponse.builder()
                .id(notifications.getId())
                .contents(notifications.getContent())
                .workspaceId(notifications.getWorkspaceId())
                .build();
    }
}
