package com.michael.notification_service.repository;

import com.michael.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusAndSendAtBefore(String status, LocalDateTime time);
    List<Notification> findByStatusAndSendPriority(
            String status, LocalDateTime time);
}
