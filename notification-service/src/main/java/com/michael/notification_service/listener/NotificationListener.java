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
        Long id = incoming.getId();
        Optional<Notification> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return;
        }
        Notification n = opt.get();
        try {
            // SYMULACJA WYSYLKI
            System.out.printf("Wysyłam %s na %s: %s%n",
                    n.getChannel(), n.getRecipient(), n.getContent());

            n.setStatus("SENT");
            repo.save(n);

        } catch (Exception ex) {
            n.setStatus("FAILED");
            repo.save(n);
        }
    }
}
