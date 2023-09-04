package com.backend.curi.notification.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.notification.controller.dto.NotificationResponse;
import com.backend.curi.notification.repository.NotificationRepository;
import com.backend.curi.notification.repository.entity.Notifications;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(Long workspaceId) {
        List<Notifications> notifications = notificationRepository.findAllByWorkspaceId(workspaceId);
        return notifications.stream()
                .map(NotificationResponse::of)
                .collect(Collectors.toList());
    }

    public void createNotification(Long workspaceId, String title, String content) {
        Notifications notification = Notifications.builder()
                .workspaceId(workspaceId)
                .title(title)
                .content(content)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public void deleteNotification(ObjectId notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public NotificationResponse markNotificationAsRead(ObjectId notificationId) {
        Notifications notification = notificationRepository.findById(notificationId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.NOTIFICATION_NOT_EXISTS));
        notification.setRead(true);
        notificationRepository.save(notification);
        return NotificationResponse.of(notification);
    }
}
