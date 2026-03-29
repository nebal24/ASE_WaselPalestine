package com.wasel.service.impl;

import com.wasel.entity.Alert;
import com.wasel.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class DefaultNotificationServiceImpl implements NotificationService {

    @Override
    public void send(Alert alert) {
        // No external service connected yet.
    }
}