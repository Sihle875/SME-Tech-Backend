package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Assertions;

public class InputSanitizationPropertyTest {

    @Property
    void trimmedStringHasNoLeadingOrTrailingWhitespace(@ForAll String input) {
        String trimmed = input.trim();
        Assertions.assertFalse(trimmed.startsWith(" "), "Trimmed string starts with space");
        Assertions.assertFalse(trimmed.startsWith("\t"), "Trimmed string starts with tab");
        Assertions.assertFalse(trimmed.endsWith(" "), "Trimmed string ends with space");
        Assertions.assertFalse(trimmed.endsWith("\t"), "Trimmed string ends with tab");
    }

    @Property
    void emailTrimIsIdempotent(@ForAll @StringLength(min = 1, max = 100) String email) {
        String trimmed = email.trim();
        Assertions.assertEquals(trimmed, trimmed.trim(), "Trim is not idempotent");
    }
}
