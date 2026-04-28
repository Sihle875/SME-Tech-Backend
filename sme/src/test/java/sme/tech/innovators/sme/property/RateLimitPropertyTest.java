package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.util.ReflectionTestUtils;
import sme.tech.innovators.sme.exception.RateLimitExceededException;
import sme.tech.innovators.sme.service.RateLimitService;

public class RateLimitPropertyTest {

    private RateLimitService createService() {
        RateLimitService service = new RateLimitService();
        ReflectionTestUtils.setField(service, "ipMaxAttempts", 5);
        ReflectionTestUtils.setField(service, "emailMaxAttempts", 3);
        ReflectionTestUtils.setField(service, "windowMinutes", 60);
        ReflectionTestUtils.setField(service, "blockDurationMinutes", 60);
        return service;
    }

    @Property(tries = 10)
    void ipIsBlockedAfter5Attempts(@ForAll("ipAddresses") String ip) {
        RateLimitService service = createService();
        // Make 5 attempts
        for (int i = 0; i < 5; i++) {
            service.incrementAttempt(ip, null);
        }
        // 6th should be blocked
        Assertions.assertThrows(RateLimitExceededException.class,
                () -> service.checkRateLimit(ip, null));
    }

    @Property(tries = 10)
    void emailIsBlockedAfter3Attempts(@ForAll("emails") String email) {
        RateLimitService service = createService();
        String ip = "192.168.1.1";
        for (int i = 0; i < 3; i++) {
            service.incrementAttempt(ip, email);
        }
        Assertions.assertThrows(RateLimitExceededException.class,
                () -> service.checkRateLimit("10.0.0.1", email));
    }

    @Property(tries = 10)
    void counterResetsOnSuccess(@ForAll("ipAddresses") String ip) {
        RateLimitService service = createService();
        for (int i = 0; i < 3; i++) {
            service.incrementAttempt(ip, null);
        }
        service.resetAttempts(ip, null);
        Assertions.assertDoesNotThrow(() -> service.checkRateLimit(ip, null));
    }

    @Provide
    Arbitrary<String> ipAddresses() {
        return Arbitraries.of("10.0.0.1", "10.0.0.2", "192.168.1.1", "172.16.0.1", "127.0.0.1");
    }

    @Provide
    Arbitrary<String> emails() {
        return Arbitraries.of("a@test.com", "b@test.com", "c@test.com", "d@test.com", "e@test.com");
    }
}
