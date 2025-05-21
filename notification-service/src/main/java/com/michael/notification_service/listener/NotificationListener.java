package com.michael.notification_service.listener;

import com.michael.notification_service.config.RabbitConfig;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry registry;

    public NotificationListener(NotificationRepository repo, MeterRegistry registry) {
        this.repo = repo;
        this.registry = registry;
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
        try {
            n.setStatus("SENT");
            repo.save(n);

            Counter.builder("notifications.count")
                    .tag("status", "SENT")
                    .register(registry)
                    .increment();

        } catch (Exception e) {
            n.setStatus("FAILED");
            repo.save(n);

            Counter.builder("notifications.count")
                    .tag("status", "FAILED")
                    .register(registry)
                    .increment();
            throw e;
        }
    }

    @Recover
    public void recover(RuntimeException e, Notification incoming) {
        Notification n = repo.findById(incoming.getId()).orElseThrow();
        n.setStatus("FAILED");
        repo.save(n);

        Counter.builder("notifications.count")
                .tag("status", "FAILED")
                .register(registry)
                .increment();

        throw new AmqpRejectAndDontRequeueException("Retries exhausted", e);
    }
}
