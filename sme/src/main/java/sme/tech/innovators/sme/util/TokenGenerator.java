package sme.tech.innovators.sme.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateSecureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
