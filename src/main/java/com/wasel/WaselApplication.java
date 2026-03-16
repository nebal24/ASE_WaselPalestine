package com.wasel;

import com.wasel.entity.CheckPoint;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.model.*;
import com.wasel.repository.UserRepository;
import com.wasel.service.CheckpointService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class WaselApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaselApplication.class, args);
    }

    @Bean
    CommandLineRunner run(CheckpointService checkpointService, UserRepository userRepository) {
        return args -> {

            // =============================================
            // 1. إنشاء يوزر اختباري (Admin) إذا لم يكن موجود
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
                admin.setPassword("password123");           // سيتم تشفيره لاحقاً
                admin.setRole(Role.ADMIN);
                admin.setCreatedAt(LocalDateTime.now());
                admin = userRepository.save(admin);
                System.out.println("✅ يوزر اختباري جديد تم إنشاؤه - ID: " + admin.getId());
            }

            // =============================================
            // النقطة 1: Centralized registry of checkpoints
            // =============================================
            // إنشاء 3 حواجز
            CheckPoint huwara = createCheckpoint(checkpointService, admin, "Huwara Checkpoint", 32.2, 35.3);
            CheckPoint qalandia = createCheckpoint(checkpointService, admin, "Qalandia Checkpoint", 31.8, 35.2);
            CheckPoint beitEl = createCheckpoint(checkpointService, admin, "Beit El Checkpoint", 31.9, 35.2);

            // ربط حادث (Incident) مع حاجز (مثال على road closure / accident)
            Incident accident = new Incident();
            accident.setDescription("حادث سير كبير عند البوابة الرئيسية");
            accident.setCategory(IncidentCategory.ACCIDENT);
            accident.setSeverity(IncidentSeverity.HIGH);
            accident.setLatitude(32.2);
            accident.setLongitude(35.3);
            checkpointService.createIncidentForCheckpoint(huwara.getId(), accident, admin.getId());
            System.out.println("✅ Incident مرتبط بحاجز Huwara تم إنشاؤه");

            // =============================================
            // النقطة 2: Status History
            // =============================================
            checkpointService.updateStatus(huwara.getId(), CheckpointStatus.DELAYED, admin.getId());
            checkpointService.updateStatus(qalandia.getId(), CheckpointStatus.CLOSED, admin.getId());

            System.out.println("\n🎉 النقطتين 1 و 2 تم تنفيذهما بالكامل!");
            System.out.println("   • 3 حواجز تم إنشاؤها");
            System.out.println("   • Incident مرتبط مع حاجز");
            System.out.println("   • تاريخ الحالات (Status History) تم تسجيله");

            // عرض التاريخ للتأكيد
            checkpointService.getStatusHistory(huwara.getId()).forEach(history -> {
                System.out.println("   → تاريخ Huwara: " + history.getStatus() +
                        " | " + history.getUpdatedAt() +
                        " | By User: " + history.getUpdatedBy().getName());
            });
        };
    }

    // Helper method لإنشاء Checkpoint
    private CheckPoint createCheckpoint(CheckpointService service, User user, String name, double lat, double lng) {
        CheckPoint cp = new CheckPoint();
        cp.setName(name);
        cp.setLatitude(lat);
        cp.setLongitude(lng);
        CheckPoint saved = service.createCheckpoint(cp, user.getId());
        System.out.println("✅ Checkpoint تم إنشاؤه: " + name);
        return saved;
    }
}