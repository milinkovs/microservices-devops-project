package com.example.notification_service;

import com.example.notification_service.controller.NotificationController;
import com.example.notification_service.service.NotificationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationListener notificationListener;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHistory_ShouldReturnNotificationList() {

        List<String> mockHistory = Arrays.asList(
                "Poruka 1",
                "Poruka 2"
        );

        when(notificationListener.getNotificationHistory())
                .thenReturn(mockHistory);

        List<String> result = notificationController.getHistory();

        assertEquals(2, result.size());
        assertEquals("Poruka 1", result.get(0));

        verify(notificationListener, times(1))
                .getNotificationHistory();
    }
}