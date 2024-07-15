package com.enset.studentspaymentsmanagement.web;

import com.enset.studentspaymentsmanagement.entities.Notification;
import com.enset.studentspaymentsmanagement.repositories.NotificationRepository;
import com.enset.studentspaymentsmanagement.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/admin/notifications")
    public List<Notification> getAdminNotifications() {
        return notificationService.getAdminNotifications();
    }

    @GetMapping("/student/notifications/{studentCode}")
    public List<Notification> getStudentNotifications(@PathVariable String studentCode) {
        return notificationService.getStudentNotifications(studentCode);
    }

    @PostMapping("/notifications/{id}/markAsRead")
    public void markAsRead(@PathVariable("id") Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(null);
        notification.setSeen(true);
        notificationRepository.save(notification);
        notificationRepository.delete(notification);
    }
}
