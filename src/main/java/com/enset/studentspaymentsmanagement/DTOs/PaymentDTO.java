package com.enset.studentspaymentsmanagement.DTOs;

import com.enset.studentspaymentsmanagement.entities.PaymentStatus;
import com.enset.studentspaymentsmanagement.entities.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private LocalDate paymentDate;
    private double amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String paymentFile;
    private SimpleStudentDTO simpleStudentDTO;
}
