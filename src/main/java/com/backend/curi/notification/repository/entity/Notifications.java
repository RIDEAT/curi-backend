package com.backend.curi.notification.repository.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Notifications {
    @Id
    private ObjectId id;
    @Setter
    private Object content;

    private Long workspaceId;
}
