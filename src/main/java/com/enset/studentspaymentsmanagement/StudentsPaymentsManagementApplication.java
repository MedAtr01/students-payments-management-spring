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
        SpringApplication.run(StudentsPaymentsManagementApplication.class, args);
        Dotenv dotenv = Dotenv.load();
        setSystemProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
        setSystemProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        setSystemProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        setSystemProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME"));
        setSystemProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));
        setSystemProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        setSystemProperty("application.mailing.frontend.activation-url", dotenv.get("APPLICATION_MAILING_FRONTEND_ACTIVATION_URL"));


    }

    private static void setSystemProperty(String key, String value) {
        if (System.getProperty(key) == null && value != null) {
            System.setProperty(key, value);
        }
    }

}

