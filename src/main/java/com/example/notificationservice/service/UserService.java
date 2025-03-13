package com.example.notificationservice.service;

import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.model.User;
import com.example.notificationservice.model.NotificationPreference;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User updateUser(String userId, User user);
    Optional<User> getUserById(String id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByPhoneNumber(String phoneNumber);
    List<User> getUsersByNotificationType(NotificationType type);
    User updateNotificationPreference(String userId, NotificationType type, boolean enabled, String deviceToken);
    void deleteUser(String id);
    List<User> getAllUsers();
    void updateNotificationPreference(String userId, NotificationPreference preference);
    void removeNotificationPreference(String userId, NotificationType type);
} 