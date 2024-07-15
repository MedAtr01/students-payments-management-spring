package com.enset.studentspaymentsmanagement.repositories;

import com.enset.studentspaymentsmanagement.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderByTimestampDesc();

    List<Notification> findNotificationBySeenIsFalseOrderByTimestampDesc();


    List<Notification> findNotificationByRecipientTypeAndSeenIsFalseOrderByTimestampDesc(String recipientType);

    List<Notification> findByRecipientTypeAndRecipientCodeAndSeenIsFalseOrderByTimestampDesc(String recipientType, String studentCode);
}
