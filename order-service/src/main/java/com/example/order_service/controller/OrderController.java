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
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

//    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate; // Za Message Queue
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public OrderController(RabbitTemplate rabbitTemplate, OrderRepository orderRepository,
                                                          WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public String placeOrder(@RequestParam Long userId, @RequestParam Long productId) {
        log.info("Primljen zahtev za novu narudžbinu. User: {}, Product: {}", userId, productId);

        // 1. Reaktivni poziv ka user-service (Port 8081)
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/users/" + userId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(obj -> true)
                .onErrorReturn(false)
                .block(); // block() koristimo da sačekamo odgovor pre nego što krenemo dalje

        log.info("Provera korisnika završena. Postoji: {}", userExists);

        // 2. Reaktivni poziv ka product-service (Port 8082)
        Boolean productExists = webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/products/" + productId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(obj -> true)
                .onErrorReturn(false)
                .block();

        log.info("Provera proizvoda završena. Postoji: {}", productExists);

        if (Boolean.TRUE.equals(userExists) && Boolean.TRUE.equals(productExists)) {

            Order order = new Order();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setOrderDate(LocalDateTime.now());
            orderRepository.save(order);
            log.info("Narudžbina uspešno sačuvana u bazu podataka.");

            // 3. Slanje poruke na RabbitMQ
            String message = "Narudžbina uspešna za korisnika " + userId + " i proizvod " + productId;
            rabbitTemplate.convertAndSend("notificationExchange", "notificationRoutingKey", message);
            log.info("Poruka poslata na Message Queue: {}", message);

            return "Order placed and saved successfully!";
        }
        log.error("Narudžbina nije uspela. Korisnik ili proizvod ne postoje.");
        return "Failed to place order. User or Product not found";
    }
}
