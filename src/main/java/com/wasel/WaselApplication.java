package com.wasel;

import com.wasel.entity.Checkpoint;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.model.*;
import com.wasel.repository.UserRepository;
//import com.wasel.service.CheckpointService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Main application class for Wasel Palestine platform
 * Bootstraps the Spring Boot application and initializes test data
 */

@SpringBootApplication
@EnableCaching
public class WaselApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaselApplication.class, args);
    }

//    /**
//     * CommandLineRunner to initialize database with test data on application startup
//     * Creates admin user, checkpoints, and sample incidents for testing
//     *
//     * @param checkpointService service for checkpoint operations
//     * @param userRepository repository for user data access
//     * @param passwordEncoder encoder for secure password hashing
//     * @return CommandLineRunner that executes after application context is loaded
//     */
//    @Bean
//    @Transactional
//    CommandLineRunner run(CheckpointService checkpointService,
//                          UserRepository userRepository,
//                          PasswordEncoder passwordEncoder) {
//        return args -> {
//
//            // =============================================
//            // 1. Create test admin user with encrypted password
//            // =============================================
//            Optional<User> existingUser = userRepository.findByEmail("admin@wasel.ps");
//            User admin;
//
//            if (existingUser.isPresent()) {
//                // Admin already exists - use existing
//                admin = existingUser.get();
//                System.out.println("Admin user already exists - ID: " + admin.getId());
//            } else {
//                // Create new admin user with encrypted password
//                admin = new User();
//                admin.setName("Admin User");
//                admin.setEmail("admin@wasel.ps");
//                admin.setPassword(passwordEncoder.encode("password123")); // Encrypt password
//                admin.setRole(Role.ADMIN);
//                admin.setCreatedAt(LocalDateTime.now());
//                admin = userRepository.save(admin);
//                System.out.println("New test admin created - ID: " + admin.getId());
//            }
//
//            // =============================================
//            // 2. Create sample checkpoints for testing
//            // =============================================
//            Checkpoint huwara = createCheckpoint(checkpointService, admin, "Huwara Checkpoint", 32.2, 35.3);
//            Checkpoint qalandia = createCheckpoint(checkpointService, admin, "Qalandia Checkpoint", 31.8, 35.2);
//            Checkpoint beitEl = createCheckpoint(checkpointService, admin, "Beit El Checkpoint", 31.9, 35.2);
//
//            // =============================================
//            // 3. Create sample incident linked to Huwara checkpoint
//            // =============================================
//            Incident accident = new Incident();
//            accident.setDescription("Major traffic accident at the main gate");
//            accident.setCategory(IncidentCategory.ACCIDENT);
//            accident.setSeverity(IncidentSeverity.HIGH);
//            accident.setLatitude(32.2);
//            accident.setLongitude(35.3);
//
//            checkpointService.createIncidentForCheckpoint(huwara.getId(), accident, admin.getId());
//            System.out.println("Incident linked to Huwara Checkpoint created");
//
//            // =============================================
//            // 4. Update checkpoint statuses for demonstration
//            // =============================================
//            checkpointService.updateStatus(huwara.getId(), CheckpointStatus.DELAYED, admin.getId());
//            checkpointService.updateStatus(qalandia.getId(), CheckpointStatus.CLOSED, admin.getId());
//
//            System.out.println("\n Features 1 and 2 (Checkpoint Management and Status History) completed!");
//        };
//    }
//
//    /**
//     * Helper method to create a checkpoint
//     *
//     * @param service CheckpointService to handle creation
//     * @param user User creating the checkpoint (admin)
//     * @param name Name of the checkpoint
//     * @param lat Latitude coordinate
//     * @param lng Longitude coordinate
//     * @return Created checkpoint entity
//     */
//    private Checkpoint createCheckpoint(CheckpointService service, User user, String name, double lat, double lng) {
//        Checkpoint cp = new Checkpoint();
//        cp.setName(name);
//        cp.setLatitude(lat);
//        cp.setLongitude(lng);
//
//        Checkpoint saved = service.createCheckpoint(cp, user.getId());
//        System.out.println("Checkpoint created: " + name);
//
//        return saved;
//    }
}