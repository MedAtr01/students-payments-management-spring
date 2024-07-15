package com.enset.studentspaymentsmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@DiscriminatorValue("STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String studentCode;
    private String program;
    private String Profile;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
