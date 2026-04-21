package com.example.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationListener {

    private static final List<String> notificationHistory = new ArrayList<>();

    @RabbitListener(queues = "notificationQueue")
    public void listen(String message) {
        log.info("NOVO OBAVEŠTENJE: Primljena poruka iz reda: {}", message);
        notificationHistory.add(message + " (Primljeno u: " + LocalDateTime.now() + ")");
    }

    public List<String> getNotificationHistory() {
        return notificationHistory;
    }

}
