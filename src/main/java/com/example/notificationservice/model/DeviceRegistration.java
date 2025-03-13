package com.example.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "device_registrations")
public class DeviceRegistration {
    @Id
    private String id;
    private String userId;
    private String deviceToken;
    private DevicePlatform platform;
    private String appVersion;
    private boolean isActive;
    
    public enum DevicePlatform {
        ANDROID,
        IOS,
        WEB
    }
} 