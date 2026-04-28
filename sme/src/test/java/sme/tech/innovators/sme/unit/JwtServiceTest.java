package sme.tech.innovators.sme.unit;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.entity.UserRole;
import sme.tech.innovators.sme.service.JwtService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expiryMinutes", 15);
    }

    private User buildUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setFullName("Test User");
        user.setAccountStatus(AccountStatus.VERIFIED);
        user.setRole(UserRole.OWNER);
        return user;
    }

    @Test
    void generatedTokenContainsCorrectClaims() {
        User user = buildUser();
        String token = jwtService.generateAccessToken(user);
        Claims claims = jwtService.extractClaims(token);

        assertEquals(user.getId().toString(), claims.get("userId"));
        assertEquals(user.getEmail(), claims.get("email"));
        assertEquals(user.getRole().name(), claims.get("role"));
    }

    @Test
    void expiredTokenFailsIsTokenValid() {
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secret",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(shortLivedService, "expiryMinutes", 0); // 0 minutes = expired immediately

        User user = buildUser();
        String token = shortLivedService.generateAccessToken(user);
        // Token with 0 minute expiry should be invalid
        assertFalse(shortLivedService.isTokenValid(token));
    }

    @Test
    void tamperedTokenFailsIsTokenValid() {
        User user = buildUser();
        String token = jwtService.generateAccessToken(user);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtService.isTokenValid(tampered));
    }

    @Test
    void extractUserIdReturnsCorrectValue() {
        User user = buildUser();
        String token = jwtService.generateAccessToken(user);
        assertEquals(user.getId(), jwtService.extractUserId(token));
    }

    @Test
    void extractRoleReturnsCorrectValue() {
        User user = buildUser();
        String token = jwtService.generateAccessToken(user);
        assertEquals(user.getRole().name(), jwtService.extractRole(token));
    }
}
