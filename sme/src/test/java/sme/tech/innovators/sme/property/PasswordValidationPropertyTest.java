package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Assertions;
import sme.tech.innovators.sme.exception.PasswordValidationException;
import sme.tech.innovators.sme.validator.PasswordValidator;

public class PasswordValidationPropertyTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Property
    void passwordShorterThan8IsRejected(@ForAll @StringLength(max = 7) String password) {
        Assertions.assertThrows(PasswordValidationException.class, () -> validator.validate(password));
    }

    @Property
    void passwordWithoutUppercaseIsRejected(@ForAll @StringLength(min = 8) @CharRange(from = 'a', to = 'z') String password) {
        // All lowercase — no uppercase
        Assertions.assertThrows(PasswordValidationException.class, () -> validator.validate(password));
    }

    @Property
    void validPasswordPasses(@ForAll("validPasswords") String password) {
        Assertions.assertDoesNotThrow(() -> validator.validate(password));
    }

    @Provide
    Arbitrary<String> validPasswords() {
        return Arbitraries.of(
            "SecurePass1!", "MyP@ssw0rd", "Hello#World9", "Test$1234Abc",
            "Valid1Pass!", "Str0ng&Pass", "P@ssword123", "Abc123!@#Def"
        );
    }
}
