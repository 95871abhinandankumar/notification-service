package com.example.notificationservice.repository;

import com.example.notificationservice.model.NotificationCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCampaignRepository extends JpaRepository<NotificationCampaign, Long> {
} 