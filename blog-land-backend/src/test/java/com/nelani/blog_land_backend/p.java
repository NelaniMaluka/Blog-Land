package com.nelani.blog_land_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PropertyInjectionTest {

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    void testJwtSecret() {
        assertNotNull(secretKey);
        System.out.println("Loaded jwt.secret: " + secretKey);
    }
}

