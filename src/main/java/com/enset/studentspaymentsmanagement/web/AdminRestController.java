package com.enset.studentspaymentsmanagement.web;

import com.enset.studentspaymentsmanagement.DTOs.PaymentDTO;
import com.enset.studentspaymentsmanagement.DTOs.StudentDTO;
import com.enset.studentspaymentsmanagement.entities.*;
import com.enset.studentspaymentsmanagement.mapper.UsersMapper;
import com.enset.studentspaymentsmanagement.repositories.PaymentRepository;
import com.enset.studentspaymentsmanagement.repositories.RoleRepository;
import com.enset.studentspaymentsmanagement.repositories.StudentRepository;
import com.enset.studentspaymentsmanagement.repositories.UserRepository;
import com.enset.studentspaymentsmanagement.security.PasswordEncoderConfig;
import com.enset.studentspaymentsmanagement.services.NotificationService;
import com.enset.studentspaymentsmanagement.services.PaymentService;
import com.enset.studentspaymentsmanagement.services.StudentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin")
public class AdminRestController {
    StudentRepository studentRepository;
    PasswordEncoderConfig passwordEncoder;
    PaymentService paymentService;
    PaymentRepository paymentRepository;
    UsersMapper mapper;
    UserRepository userRepository;
    RoleRepository roleRepository;
    StudentService studentService;
    PasswordEncoderConfig passwordEncoderConfig;
    NotificationService notificationService;

    public AdminRestController(StudentRepository studentRepository, UserRepository userRepository, PaymentRepository paymentRepository, PaymentService paymentService, UsersMapper mapper, StudentService studentService, NotificationService notificationService, PasswordEncoderConfig passwordEncoder) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.studentService = studentService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping(value = "/students")
    public List<StudentDTO> getStudents() {
        List<Student> students = studentRepository.findAll();
        List<StudentDTO> studentDTOs = students.stream().map(student -> mapper.fromStudent(student)).toList();
        studentDTOs.forEach(std -> {
            if (std.getProfile() != null) {
                try {
                    byte[] fileBytes = Files.readAllBytes(Path.of(URI.create(std.getProfile())));
                    std.setProfile(Base64.getEncoder().encodeToString(fileBytes));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return studentDTOs;
    }

    @GetMapping(value = "/students/{code}")
    public StudentDTO getStudentByCode(@PathVariable String code) throws IOException {
        Student student = studentRepository.findByStudentCode(code);
        StudentDTO studentDTO = mapper.fromStudent(student);
        if (student.getProfile() != null) {
            try {
                byte[] fileBytes = Files.readAllBytes(Path.of(URI.create(studentDTO.getProfile())));
                studentDTO.setProfile(Base64.getEncoder().encodeToString(fileBytes));
            } catch (IOException e) {
                throw new IOException(e);

            }
        }
        return studentDTO;


    }

    @GetMapping("/students/Program/{programId}")
    public List<Student> getStudentsByProgram(@PathVariable String programId) {
        return studentRepository.getStudentsByProgram(programId.toUpperCase());

    }

    @GetMapping("/payments")
    public ResponseEntity<?> getPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            List<Payment> payments = paymentRepository.findAll();
            List<PaymentDTO> paymentsDto = payments.stream().map(paymentDto -> mapper.fromPayment(paymentDto)).toList();
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Payments");
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                return new ResponseEntity<>(paymentsDto, headers, HttpStatus.OK);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }


    @GetMapping("/payments/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentRepository.findPaymentByPaymentId(id);
    }

    @GetMapping("/student/{code}/payments")
    public List<PaymentDTO> getPaymentsByStudentCode(@PathVariable String code) {
        List<Payment> payments = paymentRepository.findByStudentStudentCode(code);
        return payments.stream().map(payment -> mapper.fromPayment(payment)).toList();
    }

    @GetMapping("/payments/byStatus")
    public List<Payment> getPaymentsByStatus(@RequestParam PaymentStatus status) {
        return paymentRepository.findPaymentsByPaymentStatus(status);
    }

    @GetMapping("/payments/byType")
    public List<Payment> getPaymentsByType(@RequestParam PaymentType type) {
        return paymentRepository.findPaymentsByPaymentType(type);
    }

    @PutMapping("/payment/{id}/update-status")
    public ResponseEntity<Payment> updateStatus(@PathVariable long id, @RequestParam PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setPaymentStatus(status);
            paymentRepository.save(payment);
            String studentCode = payment.getStudent().getStudentCode();
            Notification notification = new Notification();
            notification.setTimestamp(LocalDateTime.now());
            notification.setRecipientType("STUDENT");
            notification.setRecipientCode(studentCode);

            String notificationMessage;

            if (status == PaymentStatus.REJECTED) {
                notificationMessage = "Payment" + payment.getPaymentId() + " has been Rejected.";
            } else {
                notificationMessage = "Payment " + payment.getPaymentId() + " has been Validated.";
            }
            notification.setMessage(notificationMessage);
            notificationService.sendNotification(notification);

            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping(value = "/paymentFile/{paymentId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_PDF_VALUE, MediaType.MULTIPART_MIXED_VALUE})
    public byte[] getPaymentFile(@PathVariable Long paymentId) throws IOException {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            return Files.readAllBytes(Path.of(URI.create(payment.getPaymentFile())));
        } else throw new RuntimeException("File nonexistent");
    }

    @PostMapping(value = "/student/{code}/addPayment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_PDF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<?> addPaymentToStudent(@PathVariable String code, @RequestParam MultipartFile file, double amount, String paymentDate, PaymentType type, PaymentStatus status) throws IOException {
        try {

            Student student = studentRepository.findByStudentCode(code);
            Payment createdPayment = paymentService.addPayment(student, file, amount, paymentDate, type, status);
            return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
        } catch (UsernameNotFoundException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PutMapping(value = "/students/{code}/Update-Info")
    public ResponseEntity<?> updateStudentInfo(@PathVariable("code") String studentCode,
                                               @RequestParam("username") String username,
                                               @RequestParam("email") String email,
                                               @RequestParam("fullName") String fullName,
                                               @RequestParam("program") String program) {
        User userToUpdate = studentRepository.findByStudentCode(studentCode).getUser();
        studentService.updateStudentInfo(username, email, fullName, studentCode, program, userToUpdate);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "students/{username}/update-password")
    public ResponseEntity<?> updateStudentPassword(@PathVariable("username") String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User userToUpdate = user.get();
            userToUpdate.setPassword(passwordEncoder.passwordEncoder().encode(password));
            userRepository.save(userToUpdate);
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(404).body(null);

    }


    @PostMapping(value = "/create-user")
    public ResponseEntity<?> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String roleName) {
        try {
            Role role = roleRepository.findByRoleName(roleName).get();

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoderConfig.passwordEncoder().encode(password));
            user.setRole(role);
            user.setEnabled(true);
            user.setAccountLocked(false);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


    @DeleteMapping("/deleteStudent/{code}")
    public ResponseEntity<?> deleteStudent(@PathVariable String code) {
        Student student = studentRepository.findByStudentCode(code);
        if (student != null) {
            studentRepository.delete(student);
            return ResponseEntity.ok().build();
        } else
            return (ResponseEntity<?>) ResponseEntity.notFound();
    }
}
