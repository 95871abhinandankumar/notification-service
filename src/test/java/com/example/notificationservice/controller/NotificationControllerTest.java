package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationRequest;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.FCMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private FCMService fcmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendNotification_ShouldReturnSuccess() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setType(NotificationType.PUSH);
        request.setRecipient("test@example.com");
        request.setToken("test-token");
        request.setTitle("Test Title");
        request.setBody("Test Body");

        // Mock the service to do nothing when sendNotification is called
        doNothing().when(notificationService).sendNotification(any(NotificationRequest.class));

        // When & Then
        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"message\":\"Notification sent successfully\"}"));
    }

    @Test
    void sendNotification_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 