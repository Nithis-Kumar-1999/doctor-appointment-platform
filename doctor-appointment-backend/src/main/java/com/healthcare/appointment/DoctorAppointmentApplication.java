package com.healthcare.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Doctor Appointment Management System.
 *
 * <p>{@code @SpringBootApplication} is a convenience annotation that combines:
 * <ul>
 *   <li>{@code @Configuration} - marks this as a Spring configuration class</li>
 *   <li>{@code @EnableAutoConfiguration} - enables Spring Boot's auto-configuration</li>
 *   <li>{@code @ComponentScan} - scans all components in this package and sub-packages</li>
 * </ul>
 *
 * <p>{@code @EnableJpaAuditing} activates JPA Auditing so that {@code @CreatedDate},
 * {@code @LastModifiedDate}, {@code @CreatedBy}, and {@code @LastModifiedBy}
 * annotations on entity fields are automatically populated.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class DoctorAppointmentApplication {

    /**
     * Main method — the JVM entry point.
     *
     * <p>Spring Boot creates an ApplicationContext, auto-configures all beans,
     * and starts the embedded Tomcat server.
     *
     * @param args command-line arguments (e.g., --spring.profiles.active=prod)
     */
    public static void main(String[] args) {
        SpringApplication.run(DoctorAppointmentApplication.class, args);
    }
}
