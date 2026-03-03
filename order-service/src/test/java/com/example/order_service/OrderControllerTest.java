package com.example.order_service;

import com.example.order_service.controller.OrderController;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));
    }

    @Test
    void placeOrder_ShouldSaveOrderAndSendMessage_WhenUserAndProductExist() {

        String result = orderController.placeOrder(1L, 2L);

        assertEquals("Order placed and saved successfully!", result);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("notificationExchange"),
                        eq("notificationRoutingKey"),
                        anyString());
    }
}