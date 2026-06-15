package io.github.fatima797.expensetracker.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Key;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import io.github.fatima797.expensetracker.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtAuthenticationFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String validToken;

    @Value("${jwt.secret}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("Password123!"));
        userRepository.save(testUser);
        validToken = jwtService.generateToken(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void validToken_ShouldReturn200Ok() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + validToken))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.publicId").exists())
                .andExpect(jsonPath("$.password").doesNotExist());

    }

    @Test
    void noAuthorizationHeader_ShouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))

                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"));
    }

    @Test
    void expiredToken_ShouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + generateExpiredToken("test@example.com")))

                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Token Expired"))
                .andExpect(jsonPath("$.message").value("Your session has expired. Please log in again."));
    }

    private String generateExpiredToken(String email) {
        // Issued 15 minutes ago, expired 5 seconds ago

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 900000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(signingKey)
                .compact();
    }

    @Test
    void invalidToken_ShouldReturn401Unauthorized() throws Exception {
        String invalidToken = validToken + "tampered";

        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + invalidToken))

                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid token. Please log in again."));
    }

    @Test
    void malformedAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "InvalidFormat"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"));
    }

}
