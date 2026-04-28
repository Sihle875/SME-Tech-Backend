package sme.tech.innovators.sme.unit;

import org.junit.jupiter.api.Test;
import sme.tech.innovators.sme.exception.PasswordValidationException;
import sme.tech.innovators.sme.validator.PasswordValidator;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void emptyPasswordIsRejected() {
        assertThrows(PasswordValidationException.class, () -> validator.validate(""));
    }

    @Test
    void nullPasswordIsRejected() {
        assertThrows(PasswordValidationException.class, () -> validator.validate(null));
    }

    @Test
    void passwordWithoutUppercaseIsRejected() {
        PasswordValidationException ex = assertThrows(PasswordValidationException.class,
                () -> validator.validate("lowercase1!"));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.contains("uppercase")));
    }

    @Test
    void passwordWithoutLowercaseIsRejected() {
        PasswordValidationException ex = assertThrows(PasswordValidationException.class,
                () -> validator.validate("UPPERCASE1!"));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.contains("lowercase")));
    }

    @Test
    void passwordWithoutDigitIsRejected() {
        PasswordValidationException ex = assertThrows(PasswordValidationException.class,
                () -> validator.validate("NoDigit!!"));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.contains("digit")));
    }

    @Test
    void passwordWithoutSpecialCharIsRejected() {
        PasswordValidationException ex = assertThrows(PasswordValidationException.class,
                () -> validator.validate("NoSpecial1"));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.contains("special")));
    }

    @Test
    void validPasswordPasses() {
        assertDoesNotThrow(() -> validator.validate("SecurePass1!"));
    }

    @Test
    void multipleFailuresReportedTogether() {
        PasswordValidationException ex = assertThrows(PasswordValidationException.class,
                () -> validator.validate("short"));
        assertTrue(ex.getErrors().size() > 1, "Expected multiple errors for 'short'");
    }
}
