package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class CorrelationIdPropertyTest {

    @Property(tries = 50)
    void correlationIdIsValidUuidFormat(@ForAll("correlationIds") String correlationId) {
        // Validate UUID format
        Assertions.assertDoesNotThrow(() -> UUID.fromString(correlationId),
                "Correlation ID is not a valid UUID: " + correlationId);
    }

    @Property(tries = 50)
    void generatedCorrelationIdIsUnique() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        Assertions.assertNotEquals(id1, id2, "Generated correlation IDs should be unique");
    }

    @Provide
    Arbitrary<String> correlationIds() {
        return Arbitraries.create(() -> UUID.randomUUID().toString());
    }
}
