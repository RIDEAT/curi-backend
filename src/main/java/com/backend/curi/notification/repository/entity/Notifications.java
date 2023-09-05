package com.backend.curi.notification.repository.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Notifications {
    @Id
    private ObjectId id;
    private String title;
    private LocalDateTime timestamp;
    @Setter
    private Object content;

    @Builder.Default
    @Setter
    private boolean isRead = false;

    private Long workspaceId;
}
