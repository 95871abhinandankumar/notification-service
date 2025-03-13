package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationRequest;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.FCMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FCMService fcmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendNotification_ShouldReturnSuccess() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setType(NotificationType.PUSH);
        request.setToken("test-token");
        request.setTitle("Test Title");
        request.setBody("Test Body");

        doNothing().when(fcmService).sendMessageToToken(any(NotificationRequest.class));

        // When & Then
        mockMvc.perform(post("/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification has been sent."));
    }

    @Test
    void sendNotification_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 