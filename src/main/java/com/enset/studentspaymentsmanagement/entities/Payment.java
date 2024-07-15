package com.enset.studentspaymentsmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity @NoArgsConstructor @AllArgsConstructor @Data
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private LocalDate paymentDate;
    private double amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String paymentFile;
    @ManyToOne
    private Student student;
}
