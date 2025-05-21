package com.michael.notification_service.service;

import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import com.michael.notification_service.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {

    private final NotificationRepository repo;
    private final RabbitTemplate rabbit;

    public NotificationScheduler(NotificationRepository repo, RabbitTemplate rabbit) {
        this.repo = repo;
        this.rabbit = rabbit;
    }

    // co 10 sekund
    @Scheduled(initialDelay = 10_000, fixedRate = 10_000)
    @Transactional
    public void dispatchDueNotifications() {
        List<Notification> due = repo.findByStatusAndSendAtBefore("PENDING", LocalDateTime.now());
        for (Notification n : due) {
            rabbit.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.ROUTING_KEY,
                    n
            );
            n.setStatus("IN_QUEUE");
            repo.save(n);
        }
    }
}
