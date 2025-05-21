package com.michael.notification_service.service;

import com.michael.notification_service.dto.CreateNotificationRequest;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Notification create(CreateNotificationRequest dto) {
        Notification n = new Notification();
        n.setRecipient(dto.getRecipient());
        n.setChannel(dto.getChannel());
        n.setContent(dto.getContent());
        n.setSendAt(dto.getSendAt());
        n.setPriority(dto.getPriority() != null ? dto.getPriority() : 1);
        n.setStatus("PENDING");
        return repo.save(n);
    }

    public String getStatus(Long id) {
        return repo.findById(id)
                .map(Notification::getStatus)
                .orElse("NOT_FOUND");
    }
}