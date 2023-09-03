package com.backend.curi.notification.controller.dto;

import com.backend.curi.notification.repository.entity.Notifications;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    @JsonSerialize(using = ToStringSerializer.class)
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
