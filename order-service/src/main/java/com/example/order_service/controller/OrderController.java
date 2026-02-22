package com.example.order_service.controller;

import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate; // Za Message Queue
    private final OrderRepository orderRepository;

    public OrderController(RestTemplate restTemplate, RabbitTemplate rabbitTemplate, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public String placeOrder(@RequestParam Long userId, @RequestParam Long productId) {
        log.info("Primljen zahtev za novu narudžbinu. User: {}, Product: {}", userId, productId);

        // 1. REST poziv ka user-service
        Object user = restTemplate.getForObject("http://localhost:8081/api/users/" + userId, Object.class);

        // 2. REST poziv ka product-service
        Object product = restTemplate.getForObject("http://localhost:8082/api/products/" + productId, Object.class);

        if (user != null && product != null) {

            Order order = new Order();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setOrderDate(LocalDateTime.now());
            orderRepository.save(order);

            // 3. Slanje poruke na RabbitMQ
            String message = "Narudžbina uspešna za korisnika " + userId;
            rabbitTemplate.convertAndSend("notificationExchange", "notificationRoutingKey", message);
            log.info("Poruka poslata na Message Queue: {}", message);
            return "Order placed successfully!";
        }
        return "Failed to place order.";
    }
}
