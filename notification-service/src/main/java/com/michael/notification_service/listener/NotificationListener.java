package com.michael.notification_service.listener;

import com.michael.notification_service.config.RabbitConfig;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class NotificationListener {

    private final NotificationRepository repo;

    public NotificationListener(NotificationRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Transactional
    public void onMessage(Notification incoming) {
        Notification n = repo.findById(incoming.getId()).orElse(null);
        if (n == null) return;
        try {
            // tu wysyłasz e-mail/logujesz
//            Thread.sleep(3000);
            n.setStatus("SENT");
        } catch (Exception e) {
            n.setStatus("FAILED");
        }
        repo.save(n);
    }
}
