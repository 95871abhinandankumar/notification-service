package com.example.notificationservice.service;

import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.model.User;
import com.example.notificationservice.model.NotificationPreference;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByPhoneNumber(String phoneNumber);
    User createUser(User user);
    User updateUser(Long userId, User user);
    void deleteUser(Long id);
    List<User> getAllUsers();
    void updateNotificationPreference(Long userId, NotificationPreference preference);
    void updateNotificationPreference(Long userId, NotificationType type, boolean enabled, String deviceToken);
    void removeNotificationPreference(Long userId, NotificationType type);
} 