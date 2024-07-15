package com.enset.studentspaymentsmanagement.mapper;

import com.enset.studentspaymentsmanagement.DTOs.*;
import com.enset.studentspaymentsmanagement.entities.Payment;
import com.enset.studentspaymentsmanagement.entities.Role;
import com.enset.studentspaymentsmanagement.entities.Student;
import com.enset.studentspaymentsmanagement.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Mapper(componentModel = "spring")
public interface UsersMapper {

    StudentDTO fromStudent(Student student);

    PaymentDTO fromPayment(Payment payment);

    Student fromStudentDto(StudentDTO studentDTO);

    SimpleStudentDTO toSimpleStudentDTO(StudentDTO studentDTO);

    UserDTO fromUser(User user);

    User fromUserDto(UserDTO userDTO);

    RoleDTO fromRole(Role role);

    Role fromRoleDto(RoleDTO roleDTO);
}
