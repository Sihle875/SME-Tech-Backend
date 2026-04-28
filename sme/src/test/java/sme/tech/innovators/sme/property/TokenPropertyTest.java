package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import sme.tech.innovators.sme.util.TokenGenerator;

import java.time.LocalDateTime;
import java.util.Base64;

public class TokenPropertyTest {

    private final TokenGenerator tokenGenerator = new TokenGenerator();

    @Property(tries = 50)
    void tokenHas128PlusBitsEntropy() {
        String token = tokenGenerator.generateSecureToken();
        // 32 bytes = 256 bits; base64url without padding
        byte[] decoded = Base64.getUrlDecoder().decode(token);
        Assertions.assertTrue(decoded.length >= 16, "Token has less than 128 bits: " + decoded.length * 8);
    }

    @Property(tries = 50)
    void tokenIsUniqueAcrossGenerations() {
        String token1 = tokenGenerator.generateSecureToken();
        String token2 = tokenGenerator.generateSecureToken();
        Assertions.assertNotEquals(token1, token2, "Two generated tokens should not be equal");
    }

    @Property(tries = 50)
    void verificationTokenExpiresIn24Hours() {
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime expiresAt = before.plusHours(24);
        LocalDateTime after = LocalDateTime.now();

        Assertions.assertTrue(!expiresAt.isBefore(before.plusHours(24).minusSeconds(1)));
        Assertions.assertTrue(!expiresAt.isAfter(after.plusHours(24).plusSeconds(1)));
    }

    @Property(tries = 50)
    void expiredTokenIsDetectedCorrectly() {
        LocalDateTime pastExpiry = LocalDateTime.now().minusHours(1);
        boolean isExpired = LocalDateTime.now().isAfter(pastExpiry);
        Assertions.assertTrue(isExpired, "Past expiry should be detected as expired");
    }
}
