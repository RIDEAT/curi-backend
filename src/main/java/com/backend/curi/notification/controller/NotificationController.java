package com.backend.curi.notification.controller;

import com.backend.curi.notification.controller.dto.DeleteResponse;
import com.backend.curi.notification.controller.dto.NotificationResponse;
import com.backend.curi.notification.service.NotificationService;
import com.backend.curi.workflow.controller.dto.RequiredForLaunchResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>>getNotifications(@PathVariable Long workspaceId){
        var response = notificationService.getNotifications(workspaceId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/notifications/{notificationId}")
    public ResponseEntity<DeleteResponse> deleteNotification(@PathVariable ObjectId notificationId){
        notificationService.deleteNotification(notificationId);
        DeleteResponse deleteResponse = new DeleteResponse(true);
        return ResponseEntity.ok(deleteResponse);
    }

    @PutMapping("/notifications/{notificationId}/mark-as-read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(
            @PathVariable ObjectId notificationId) {
        NotificationResponse response = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(response);
    }

}
