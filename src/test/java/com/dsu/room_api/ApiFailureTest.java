package com.dsu.room_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiFailureTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void ensureSamplePasswordsMatch() {
        updatePassword("mina", "mina1234");
        updatePassword("omar", "omar1234");
        updatePassword("admin", "admin1234");
    }

    private void updatePassword(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            org.springframework.test.util.ReflectionTestUtils.setField(
                    user, "passwordHash", passwordEncoder.encode(rawPassword));
            userRepository.saveAndFlush(user);
        });
    }

    private static String basic(String username) {
        String pair = username + ":" + username + "1234";
        return "Basic " + Base64.getEncoder()
                .encodeToString(pair.getBytes(StandardCharsets.UTF_8));
    }

    // --- Step 8: 3 Core Security & Happy-Path Tests ---

    @Test
    void noLoginIs401() throws Exception {
        mvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("login required"));
    }

    @Test
    void userCreatingRoomIs403() throws Exception {
        mvc.perform(post("/api/rooms")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Test Room", "capacity": 5 }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("admin role required"));
    }

    @Test
    void adminCreatingRoomIs201() throws Exception {
        mvc.perform(post("/api/rooms")
                        .header("Authorization", basic("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Test Room", "capacity": 5 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    // --- Lab H2: Remaining Failures of R7 ---

    @Test
    void overlapIs409() throws Exception {
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("omar"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startTime": "2026-10-06T10:30:00",
                                  "endTime": "2026-10-06T11:30:00"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("already booked"));
    }

    @Test
    void endBeforeStartIs400() throws Exception {
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startTime": "2026-10-06T12:00:00",
                                  "endTime": "2026-10-06T11:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("startTime must be before endTime"));
    }

    @Test
    void anotherUsersReservationIs403() throws Exception {
        mvc.perform(get("/api/reservations/1")
                        .header("Authorization", basic("omar")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("reservation 1 belongs to another user"));
    }

    @Test
    void deleteRoomWithReservationsIs409() throws Exception {
        mvc.perform(delete("/api/rooms/1")
                        .header("Authorization", basic("admin")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("room 1 has reservations"));
    }

    @Test
    void missingFieldIs400() throws Exception {
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startTime": "2026-10-08T10:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("endTime"));
    }
}