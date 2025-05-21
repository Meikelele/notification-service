package com.michael.notification_service.controller;

import com.michael.notification_service.dto.CreateNotificationRequest;
import com.michael.notification_service.entity.Notification;
import com.michael.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService svc;
    public NotificationController(NotificationService svc) {
        this.svc = svc;
    }
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody CreateNotificationRequest dto) {
        Notification created = svc.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/{id}/status")
    public ResponseEntity<String> status(@PathVariable Long id) {
        String status = svc.getStatus(id);
        if ("NOT_FOUND".equals(status)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
