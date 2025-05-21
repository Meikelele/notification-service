package com.michael.notification_service.service;

import com.michael.notification_service.dto.CreateNotificationRequest;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    private final RabbitTemplate rabbit;

    public NotificationService(NotificationRepository repo, RabbitTemplate rabbit) {
        this.repo = repo;
        this.rabbit = rabbit;
    }

    @Transactional
    public Notification createAndSend(CreateNotificationRequest dto) {

        Notification n = new Notification();
        n.setRecipient(dto.getRecipient());
        n.setChannel(dto.getChannel());
        n.setContent(dto.getContent());
        n.setSendAt(dto.getSendAt());
        n.setPriority(dto.getPriority() != null ? dto.getPriority() : 1);
        n.setStatus("PENDING");

        Notification saved = repo.save(n);


        rabbit.convertAndSend(
                "notifications.exchange",
                "notifications.routingkey",
                saved
        );

        return saved;
    }

    public String getStatus(Long id) {
        return repo.findById(id)
                .map(Notification::getStatus)
                .orElse("NOT_FOUND");
    }
}
