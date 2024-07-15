package com.enset.studentspaymentsmanagement.web;

import com.enset.studentspaymentsmanagement.DTOs.StudentDTO;
import com.enset.studentspaymentsmanagement.entities.*;
import com.enset.studentspaymentsmanagement.mapper.UsersMapper;
import com.enset.studentspaymentsmanagement.repositories.PaymentRepository;
import com.enset.studentspaymentsmanagement.repositories.StudentRepository;
import com.enset.studentspaymentsmanagement.services.PaymentService;
import com.enset.studentspaymentsmanagement.services.StudentService;
import com.enset.studentspaymentsmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("web")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final UserService userService;
    private final UsersMapper mapper;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping(value = "/addPayment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_PDF_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPayment(@AuthenticationPrincipal Jwt jwt, @RequestParam MultipartFile file, double amount, String paymentDate, PaymentType type, PaymentStatus status) throws IOException {
        try {
            User user = userService.getAuthenticatedUser();
            Student student = studentRepository.findStudentByUser(user);
            Payment createdPayment = paymentService.addPayment(student, file, amount, paymentDate, type, status);
            return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
        } catch (UsernameNotFoundException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PutMapping(value = "/Update-Info")
    public ResponseEntity<?> updateStudentInfo(@AuthenticationPrincipal Jwt jwt,
                                               @RequestParam("username") String username,
                                               @RequestParam("email") String email,
                                               @RequestParam("fullName") String fullName,
                                               @RequestParam("studentCode") String studentCode,
                                               @RequestParam("program") String program) {
        User userToUpdate = userService.getAuthenticatedUser();
        studentService.updateStudentInfo(username, email, fullName, studentCode, program, userToUpdate);
        return ResponseEntity.ok().build();


    }


    @GetMapping(value = "/student-payments", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getStudentPayments() {
        try {
            User user = userService.getAuthenticatedUser();

            Student student = studentRepository.findStudentByUser(user);
            List<Payment> payments = paymentRepository.findByStudentStudentCode(student.getStudentCode());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            return new ResponseEntity<>(payments, headers, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @GetMapping("/student")
    public ResponseEntity<?> getStudentByUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            User user = userService.getAuthenticatedUser();
            Student student = studentRepository.findStudentByUser(user);

            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }

            StudentDTO studentDTO = mapper.fromStudent(student);
            if (student.getProfile() != null) {
                try {
                    byte[] fileBytes = Files.readAllBytes(Path.of(URI.create(studentDTO.getProfile())));
                    studentDTO.setProfile(Base64.getEncoder().encodeToString(fileBytes));
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read profile image");
                }
            }

            return ResponseEntity.ok(studentDTO);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }


    @PostMapping(value = "/Update-Info/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<?> updateStudentInfo(
            @RequestParam("profile") MultipartFile profile
    ) throws IOException {
        try {
            User userToUpdate = userService.getAuthenticatedUser();

            Student student = studentRepository.findStudentByUser(userToUpdate);

            student.setProfile(studentService.profileCreation(profile));
            studentRepository.save(student);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(value = "student-payments/{paymentId}/paymentFile", produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] getPaymentFile(@PathVariable Long paymentId) throws IOException {
        Payment payment = paymentRepository.findById(paymentId).get();
        return Files.readAllBytes(Path.of(URI.create(payment.getPaymentFile())));

    }

    @DeleteMapping("/deletePayment/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        Payment payment = paymentRepository.findPaymentByPaymentId(id);
        if (payment != null) {
            paymentRepository.delete(payment);
            return ResponseEntity.ok().build();
        } else
            return (ResponseEntity<?>) ResponseEntity.notFound();

    }
}
