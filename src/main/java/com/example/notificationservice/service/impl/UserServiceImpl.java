package com.example.notificationservice.service.impl;

import com.example.notificationservice.service.UserService;
import com.example.notificationservice.model.User;
import com.example.notificationservice.model.NotificationPreference;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.repository.UserRepository;
import com.example.notificationservice.exception.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<User> getUsersByNotificationType(NotificationType type) {
        return userRepository.findByNotificationPreferencesTypeAndNotificationPreferencesEnabledTrue(type);
    }

    @Override
    public User updateNotificationPreference(String userId, NotificationType type, boolean enabled, String deviceToken) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotificationException("User not found: " + userId));
        
        user.getNotificationPreferences().stream()
            .filter(pref -> pref.getType() == type)
            .findFirst()
            .ifPresent(pref -> {
                pref.setEnabled(enabled);
                if (deviceToken != null) {
                    pref.setDeviceToken(deviceToken);
                }
            });
        
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            throw new NotificationException("Failed to create user", e);
        }
    }

    @Override
    public User updateUser(String userId, User user) {
        if (!userRepository.existsById(userId)) {
            throw new NotificationException("User not found: " + userId);
        }
        user.setId(userId);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotificationException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateNotificationPreference(String userId, NotificationPreference preference) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotificationException("User not found: " + userId));
        
        user.getNotificationPreferences().removeIf(p -> p.getType() == preference.getType());
        user.getNotificationPreferences().add(preference);
        
        userRepository.save(user);
    }

    @Override
    public void removeNotificationPreference(String userId, NotificationType type) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotificationException("User not found: " + userId));
        
        user.getNotificationPreferences().removeIf(p -> p.getType() == type);
        userRepository.save(user);
    }
} 