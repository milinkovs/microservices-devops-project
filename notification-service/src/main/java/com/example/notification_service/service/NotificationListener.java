package com.example.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    // Ova anotacija kaže Springu da "sluša" red koji smo definisali
    @RabbitListener(queues = "notificationQueue")
    public void listen(String message) {
        log.info("NOVO OBAVEŠTENJE: Primljena poruka iz reda: {}", message);
    }

}
