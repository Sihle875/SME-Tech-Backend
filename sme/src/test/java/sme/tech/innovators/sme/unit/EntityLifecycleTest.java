package sme.tech.innovators.sme.unit;

import org.junit.jupiter.api.Test;
import sme.tech.innovators.sme.entity.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityLifecycleTest {

    @Test
    void userPrePersistSetsDefaultsCorrectly() {
        User user = new User();
        user.prePersist(); // call directly
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(AccountStatus.PENDING_VERIFICATION, user.getAccountStatus());
        assertEquals(UserRole.OWNER, user.getRole());
    }

    @Test
    void businessPrePersistSetsTimestamps() {
        Business business = new Business();
        business.prePersist();
        assertNotNull(business.getCreatedAt());
        assertNotNull(business.getUpdatedAt());
    }

    @Test
    void verificationTokenPrePersistSetsExpiresAt24HoursFromCreation() {
        VerificationToken token = new VerificationToken();
        token.prePersist();
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiresAt());
        // expiresAt should be ~24h after createdAt
        long diffHours = java.time.Duration.between(token.getCreatedAt(), token.getExpiresAt()).toHours();
        assertEquals(24, diffHours);
    }

    @Test
    void isExpiredReturnsTrueForPastExpiry() {
        VerificationToken token = new VerificationToken();
        token.setCreatedAt(LocalDateTime.now().minusDays(2));
        token.setExpiresAt(LocalDateTime.now().minusHours(1));
        assertTrue(token.isExpired());
    }

    @Test
    void isExpiredReturnsFalseForFutureExpiry() {
        VerificationToken token = new VerificationToken();
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        assertFalse(token.isExpired());
    }
}
