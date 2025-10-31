package com.movieticket.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
//
//    @Test
//    void testJwtToken() {
//        String username = "testuser";
//        Long userId = 1L;
//
//        String token = jwtUtil.generateToken(username, userId);
//
//        assertNotNull(token);
//        assertEquals(username, jwtUtil.getUsernameFromToken(token));
//        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
//        assertTrue(jwtUtil.validateToken(token));
//    }
}