package com.michael.notification_service.listener;

import com.michael.notification_service.config.RabbitConfig;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationListener {

    private final NotificationRepository repo;

    public NotificationListener(NotificationRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Retryable(
            value = RuntimeException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000)
    )
    @Transactional
    public void onMessage(Notification incoming) {
        Notification n = repo.findById(incoming.getId()).orElseThrow();
        // Symulacja błędu:
        if (incoming.getContent().contains("force-fail")) {
            throw new RuntimeException("Wywołane sztucznie dla testu retry");
        }
        // Scenariusz sukcesu:
        n.setStatus("SENT");
        repo.save(n);
    }

    @Recover
    public void recover(RuntimeException e, Notification incoming) {
        Notification n = repo.findById(incoming.getId()).orElseThrow();
        n.setStatus("FAILED");
        repo.save(n);
        throw new AmqpRejectAndDontRequeueException("Retries exhausted", e);
    }
}
