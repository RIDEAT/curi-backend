package com.backend.curi.notification.controller;

import com.backend.curi.notification.controller.dto.NotificationResponse;
import com.backend.curi.notification.service.NotificationService;
import com.backend.curi.workflow.controller.dto.RequiredForLaunchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
