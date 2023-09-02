package com.backend.curi.notification.service;

import com.backend.curi.notification.controller.dto.NotificationResponse;
import com.backend.curi.notification.repository.NotificationRepository;
import com.backend.curi.notification.repository.entity.Notifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(Long workspaceId) {
        List<Notifications> notifications = notificationRepository.findAllByWorkspaceId(workspaceId);
        return notifications.stream()
                .map(notification -> NotificationResponse.of(notification))
                .collect(Collectors.toList());
    }

    public void createNotification(Long workspaceId, String content) {
        Notifications notification = Notifications.builder()
                .workspaceId(workspaceId)
                .content(content)
                .build();
        notificationRepository.save(notification);
    }
}
