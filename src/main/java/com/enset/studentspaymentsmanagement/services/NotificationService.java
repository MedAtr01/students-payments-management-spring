package com.enset.studentspaymentsmanagement.services;

import com.enset.studentspaymentsmanagement.entities.Notification;
import com.enset.studentspaymentsmanagement.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notification", notification);
    }

    public List<Notification> getAdminNotifications() {
        return notificationRepository.findNotificationByRecipientTypeAndSeenIsFalseOrderByTimestampDesc("ADMIN");
    }

    public List<Notification> getStudentNotifications(String studentCode) {
        return notificationRepository.findByRecipientTypeAndRecipientCodeAndSeenIsFalseOrderByTimestampDesc("STUDENT", studentCode);
    }
}

