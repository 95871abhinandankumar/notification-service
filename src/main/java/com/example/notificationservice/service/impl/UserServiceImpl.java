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
    public Optional<User> getUserById(Long id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.error("Failed to get user by id: {}", e.getMessage(), e);
            throw new NotificationException("Failed to get user", e);
        }
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
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            throw new NotificationException("Failed to create user", e);
        }
    }

    @Override
    public User updateUser(Long userId, User user) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new NotificationException("User not found: " + userId);
            }
            user.setId(userId);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to update user: {}", e.getMessage(), e);
            throw new NotificationException("Failed to update user", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new NotificationException("User not found: " + id);
            }
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", e.getMessage(), e);
            throw new NotificationException("Failed to delete user", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateNotificationPreference(Long userId, NotificationPreference preference) {
        try {
            User user = getUserById(userId)
                .orElseThrow(() -> new NotificationException("User not found"));
            
            // Find existing preference or create new one
            NotificationPreference existingPreference = user.getNotificationPreferences().stream()
                .findFirst()
                .orElse(new NotificationPreference());
            
            // Update the preference values
            existingPreference.setEmailEnabled(preference.isEmailEnabled());
            existingPreference.setSmsEnabled(preference.isSmsEnabled());
            existingPreference.setPushEnabled(preference.isPushEnabled());
            existingPreference.setUser(user);
            
            // If it's a new preference, add it to the list
            if (existingPreference.getId() == null) {
                user.getNotificationPreferences().add(existingPreference);
            }
            
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to update notification preference: {}", e.getMessage(), e);
            throw new NotificationException("Failed to update notification preference", e);
        }
    }

    @Override
    public void updateNotificationPreference(Long userId, NotificationType type, boolean enabled, String deviceToken) {
        try {
            User user = getUserById(userId)
                .orElseThrow(() -> new NotificationException("User not found"));
            
            NotificationPreference preference = user.getNotificationPreferences().stream()
                .findFirst()
                .orElse(new NotificationPreference());
            
            switch (type) {
                case EMAIL -> preference.setEmailEnabled(enabled);
                case SMS -> preference.setSmsEnabled(enabled);
                case PUSH -> preference.setPushEnabled(enabled);
            }
            
            preference.setUser(user);
            if (preference.getId() == null) {
                user.getNotificationPreferences().add(preference);
                    }
            
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to update notification preference: {}", e.getMessage(), e);
            throw new NotificationException("Failed to update notification preference", e);
        }
    }

    @Override
    public void removeNotificationPreference(Long userId, NotificationType type) {
        try {
            User user = getUserById(userId)
                .orElseThrow(() -> new NotificationException("User not found"));
            
            NotificationPreference preference = user.getNotificationPreferences().stream()
                .findFirst()
                .orElse(new NotificationPreference());
            
            switch (type) {
                case EMAIL -> preference.setEmailEnabled(false);
                case SMS -> preference.setSmsEnabled(false);
                case PUSH -> preference.setPushEnabled(false);
            }
            
            preference.setUser(user);
            if (preference.getId() == null) {
                user.getNotificationPreferences().add(preference);
            }
            
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to remove notification preference: {}", e.getMessage(), e);
            throw new NotificationException("Failed to remove notification preference", e);
        }
    }
} 