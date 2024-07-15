package com.enset.studentspaymentsmanagement.repositories;

import com.enset.studentspaymentsmanagement.entities.Payment;
import com.enset.studentspaymentsmanagement.entities.PaymentStatus;
import com.enset.studentspaymentsmanagement.entities.PaymentType;
import com.enset.studentspaymentsmanagement.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findPaymentByPaymentId(Long id);
   List<Payment> findPaymentsByPaymentType(PaymentType type);
    List<Payment> findPaymentsByPaymentStatus(PaymentStatus status);
    List<Payment> findByStudentStudentCode(String code);
    List<Payment> countPaymentsByStudent(Student student);

}
