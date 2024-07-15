package com.enset.studentspaymentsmanagement.repositories;

import com.enset.studentspaymentsmanagement.entities.Student;
import com.enset.studentspaymentsmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StudentRepository extends JpaRepository<Student, String> {


    Student findByStudentCode(String code);

    Student findStudentByUser(User user);

    List<Student> getStudentsByProgram(String program);
}
