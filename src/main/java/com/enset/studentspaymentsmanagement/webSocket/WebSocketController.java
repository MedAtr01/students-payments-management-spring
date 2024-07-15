package com.enset.studentspaymentsmanagement.webSocket;

import com.enset.studentspaymentsmanagement.entities.Notification;
import com.enset.studentspaymentsmanagement.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/notifyAdmin")
    public void notifyAdmin(Notification notification) {
        notificationService.sendNotification(notification);
    }
}

