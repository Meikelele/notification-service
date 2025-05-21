package com.michael.notification_service.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class MetricsController {
    private final MeterRegistry reg;
    public MetricsController(MeterRegistry reg) { this.reg = reg; }
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return Map.of(
                "sent",    reg.counter("notifications.count", "status", "SENT").count(),
                "pending", reg.counter("notifications.count", "status", "PENDING").count(),
                "failed",  reg.counter("notifications.count", "status", "FAILED").count()
        );
    }
}

