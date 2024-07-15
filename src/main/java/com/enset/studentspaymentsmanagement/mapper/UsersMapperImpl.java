package com.enset.studentspaymentsmanagement.mapper;

import com.enset.studentspaymentsmanagement.DTOs.*;
import com.enset.studentspaymentsmanagement.entities.Payment;
import com.enset.studentspaymentsmanagement.entities.Role;
import com.enset.studentspaymentsmanagement.entities.Student;
import com.enset.studentspaymentsmanagement.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UsersMapperImpl implements UsersMapper {

    @Override
    public StudentDTO fromStudent(Student student) {
        if (student == null) {
            return null;
        }

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setUser(fromUser(student.getUser()));
        studentDTO.setStudentCode(student.getStudentCode());
        studentDTO.setProgram(student.getProgram());
        studentDTO.setId(student.getId());
        studentDTO.setProfile(student.getProfile());
        studentDTO.setFullName(student.getFullName());

        return studentDTO;
    }

    @Override
    public PaymentDTO fromPayment(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO paymentDTO = new PaymentDTO();
        StudentDTO studentDTO = fromStudent(payment.getStudent());
        paymentDTO.setSimpleStudentDTO(toSimpleStudentDTO(studentDTO));
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        paymentDTO.setPaymentType(payment.getPaymentType());
        paymentDTO.setPaymentStatus(payment.getPaymentStatus());
        paymentDTO.setPaymentFile(payment.getPaymentFile());


        return paymentDTO;
    }

    @Override
    public Student fromStudentDto(StudentDTO studentDTO) {
        if (studentDTO == null) {
            return null;
        }

        Student student = new Student();
        student.setUser(fromUserDto(studentDTO.getUser()));
        student.setStudentCode(studentDTO.getStudentCode());
        student.setProgram(studentDTO.getProgram());
        student.setId(studentDTO.getId());
        student.setProfile(studentDTO.getProfile());

        return student;
    }

    @Override
    public SimpleStudentDTO toSimpleStudentDTO(StudentDTO studentDTO) {
        if (studentDTO == null) {
            return null;
        }

        return new SimpleStudentDTO(
                studentDTO.getId(),
                studentDTO.getFullName(),
                studentDTO.getStudentCode(),
                studentDTO.getProgram(),
                studentDTO.getProfile()
        );
    }

    @Override
    public UserDTO fromUser(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setAccountLocked(user.isAccountLocked());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setRole((user.getRole()));

        return userDTO;
    }

    @Override
    public User fromUserDto(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setAccountLocked(userDTO.isAccountLocked());
        user.setEnabled(userDTO.isEnabled());
        user.setRole((userDTO.getRole()));

        return user;
    }

    @Override
    public RoleDTO fromRole(Role role) {
        if (role == null) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleId(role.getRoleId());
        roleDTO.setRoleName(role.getRoleName());

        return roleDTO;
    }

    @Override
    public Role fromRoleDto(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }

        Role role = new Role();
        role.setRoleId(roleDTO.getRoleId());
        role.setRoleName(roleDTO.getRoleName());

        return role;
    }
}
