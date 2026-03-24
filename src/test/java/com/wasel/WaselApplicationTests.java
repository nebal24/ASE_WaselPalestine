package com.wasel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/WaselPalestine",
        "spring.jpa.hibernate.ddl-auto=none"
})
class WaselApplicationTests {

    @Test
    void contextLoads() {
        // يتحقق إن الـ application context بيشتغل بدون errors
    }
}