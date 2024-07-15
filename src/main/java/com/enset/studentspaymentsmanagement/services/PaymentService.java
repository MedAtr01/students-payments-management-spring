package com.enset.studentspaymentsmanagement.services;

import com.enset.studentspaymentsmanagement.entities.*;
import com.enset.studentspaymentsmanagement.repositories.PaymentRepository;
import com.enset.studentspaymentsmanagement.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {
    @Value("${uploads.directory}")
    private String uploadsDirectory;
    StudentRepository studentRepository;
    PaymentRepository paymentRepository;
    NotificationService notificationService;

    public PaymentService(StudentRepository studentRepository, PaymentRepository paymentRepository, NotificationService notificationService) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }


    public String fileCreation(MultipartFile file) throws IOException {
        Path folderPath = Paths.get(uploadsDirectory, "data", "payments");

        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadsDirectory, "data", "payments", fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toUri().toString();
    }

    public Payment addPayment(Student student, MultipartFile file, double amount, String paymentDate, PaymentType type, PaymentStatus status) throws IOException {

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setPaymentDate(LocalDate.parse(paymentDate));
        payment.setAmount(amount);
        payment.setPaymentType(type);
        payment.setPaymentStatus(status);
        payment.setPaymentFile(fileCreation(file));
        Notification notification = new Notification();
        notification.setMessage("Payment added by " + student.getStudentCode());
        notification.setTimestamp(LocalDateTime.now());
        notification.setRecipientType("ADMIN");
        notification.setRecipientCode(student.getStudentCode());
        notificationService.sendNotification(notification);

        return paymentRepository.save(payment);
    }
}
