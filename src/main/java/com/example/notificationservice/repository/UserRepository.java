package com.example.notificationservice.repository;

import com.example.notificationservice.model.User;
import com.example.notificationservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    @Query(value = "SELECT u.* FROM #{#entityName} u " +
           "JOIN notification_preferences np ON u.id = np.user_id " +
           "WHERE np.type = :type AND np.is_enabled = true", 
           nativeQuery = true)
    List<User> findByNotificationPreferencesType(@Param("type") NotificationType type);
} 