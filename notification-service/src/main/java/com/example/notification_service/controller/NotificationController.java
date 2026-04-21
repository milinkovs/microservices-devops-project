package com.example.notification_service.controller;

import com.example.notification_service.service.NotificationListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationListener notificationListener;

    public NotificationController(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @GetMapping
    public List<String> getHistory() {
        return notificationListener.getNotificationHistory();
    }
}
