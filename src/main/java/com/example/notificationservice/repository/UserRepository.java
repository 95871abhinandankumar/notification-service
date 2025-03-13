package com.example.notificationservice.repository;

import com.example.notificationservice.model.User;
import com.example.notificationservice.model.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByNotificationPreferencesTypeAndNotificationPreferencesEnabledTrue(NotificationType type);
} 