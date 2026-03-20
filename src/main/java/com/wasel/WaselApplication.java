package com.wasel;

import com.wasel.entity.Checkpoint;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.model.*;
import com.wasel.repository.UserRepository;
import com.wasel.service.CheckpointService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class WaselApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaselApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(CheckpointService checkpointService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {  // 👈 أضيفي PasswordEncoder هنا
        return args -> {

            // =============================================
            // 1. إنشاء يوزر اختباري (Admin) مع تشفير كلمة السر
            // =============================================
            Optional<User> existingUser = userRepository.findByEmail("admin@wasel.ps");
            User admin;
            if (existingUser.isPresent()) {
                admin = existingUser.get();
                System.out.println("✅ يوزر موجود بالفعل - ID: " + admin.getId());
            } else {
                admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@wasel.ps");
                admin.setPassword(passwordEncoder.encode("password123"));  // 👈 شفري كلمة السر
                admin.setRole(Role.ADMIN);
                admin.setCreatedAt(LocalDateTime.now());
                admin = userRepository.save(admin);
                System.out.println("✅ يوزر اختباري جديد تم إنشاؤه - ID: " + admin.getId());
            }

            // =============================================
            // باقي الكود لإنشاء checkpoints والحوادث
            // =============================================
            Checkpoint huwara = createCheckpoint(checkpointService, admin, "Huwara Checkpoint", 32.2, 35.3);
            Checkpoint qalandia = createCheckpoint(checkpointService, admin, "Qalandia Checkpoint", 31.8, 35.2);
            Checkpoint beitEl = createCheckpoint(checkpointService, admin, "Beit El Checkpoint", 31.9, 35.2);

            Incident accident = new Incident();
            accident.setDescription("حادث سير كبير عند البوابة الرئيسية");
            accident.setCategory(IncidentCategory.ACCIDENT);
            accident.setSeverity(IncidentSeverity.HIGH);
            accident.setLatitude(32.2);
            accident.setLongitude(35.3);
            checkpointService.createIncidentForCheckpoint(huwara.getId(), accident, admin.getId());
            System.out.println("✅ Incident مرتبط بحاجز Huwara تم إنشاؤه");

            checkpointService.updateStatus(huwara.getId(), CheckpointStatus.DELAYED, admin.getId());
            checkpointService.updateStatus(qalandia.getId(), CheckpointStatus.CLOSED, admin.getId());

            System.out.println("\n🎉 النقطتين 1 و 2 تم تنفيذهما بالكامل!");
        };
    }

    private Checkpoint createCheckpoint(CheckpointService service, User user, String name, double lat, double lng) {
        Checkpoint cp = new Checkpoint();
        cp.setName(name);
        cp.setLatitude(lat);
        cp.setLongitude(lng);
        Checkpoint saved = service.createCheckpoint(cp, user.getId());
        System.out.println("✅ Checkpoint تم إنشاؤه: " + name);
        return saved;
    }
}