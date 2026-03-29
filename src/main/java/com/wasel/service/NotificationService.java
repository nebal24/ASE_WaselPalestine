package com.wasel.service;
import com.wasel.entity.Alert;

public interface NotificationService {
    void send(Alert alert);
}