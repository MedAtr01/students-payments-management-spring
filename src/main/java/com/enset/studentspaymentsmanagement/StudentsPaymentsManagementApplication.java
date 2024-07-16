package com.enset.studentspaymentsmanagement;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Objects;


@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"com.enset.studentspaymentsmanagement.security", "com.enset.studentspaymentsmanagement"})
public class StudentsPaymentsManagementApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        System.setProperty("spring.datasource.url", Objects.requireNonNull(dotenv.get("SPRING_DATASOURCE_URL")));
        System.setProperty("spring.datasource.username", Objects.requireNonNull(dotenv.get("SPRING_DATASOURCE_USERNAME")));
        System.setProperty("spring.datasource.password", Objects.requireNonNull(dotenv.get("SPRING_DATASOURCE_PASSWORD")));
        System.setProperty("spring.mail.username", Objects.requireNonNull(dotenv.get("SPRING_MAIL_USERNAME")));
        System.setProperty("spring.mail.password", Objects.requireNonNull(dotenv.get("SPRING_MAIL_PASSWORD")));
        System.setProperty("jwt.secret", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
        System.setProperty("application.mailing.frontend.activation-url", Objects.requireNonNull(dotenv.get("APPLICATION_MAILING_FRONTEND_ACTIVATION_URL")));
        SpringApplication.run(StudentsPaymentsManagementApplication.class, args);

    }


}

