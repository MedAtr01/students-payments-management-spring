package com.enset.studentspaymentsmanagement.services;

import com.enset.studentspaymentsmanagement.entities.Student;
import com.enset.studentspaymentsmanagement.entities.User;
import com.enset.studentspaymentsmanagement.repositories.StudentRepository;
import com.enset.studentspaymentsmanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
@RequiredArgsConstructor
public class StudentService {
    @Value("${uploads.directory}")
    private String uploadsDirectory;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;


    public String profileCreation(MultipartFile file) throws IOException {
        Path folderPath = Paths.get(uploadsDirectory, "data", "profiles");

        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadsDirectory, "data", "profiles", fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toUri().toString();
    }

    @Transactional
    public void updateStudentInfo(String username,
                                  String email,
                                  String fullName,
                                  String studentCode,
                                  String program,
                                  User userToUpdate) {

        userToUpdate.setUsername(username);
        userToUpdate.setEmail(email);
        userRepository.save(userToUpdate);

        Student student = studentRepository.findStudentByUser(userToUpdate);
        if (student == null) {
            throw new UsernameNotFoundException("Student not found");
        }
        student.setStudentCode(studentCode);
        student.setProgram(program);
        student.setFullName(fullName);
        student.setUser(userToUpdate);
        studentRepository.save(student);
    }

}
