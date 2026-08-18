package com.mall.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilTest {

    @Test
    void testGenerateAndParse() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-jwt-unit-test-1234567890");

        String token = jwtUtil.generateToken(1L, "testuser", 1, 3600);
        assertNotNull(token);

        Claims claims = jwtUtil.parseToken(token);
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
        assertEquals(1, claims.get("role"));
    }
}
