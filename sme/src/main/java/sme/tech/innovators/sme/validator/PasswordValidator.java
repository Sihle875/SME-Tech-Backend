package sme.tech.innovators.sme.validator;

import org.springframework.stereotype.Component;
import sme.tech.innovators.sme.exception.PasswordValidationException;

import java.util.ArrayList;
import java.util.List;

@Component
public class PasswordValidator {

    public void validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }
        if (password == null || !password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (password == null || !password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter");
        }
        if (password == null || !password.matches(".*[0-9].*")) {
            errors.add("Password must contain at least one digit");
        }
        if (password == null || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.add("Password must contain at least one special character");
        }

        if (!errors.isEmpty()) {
            throw new PasswordValidationException(errors);
        }
    }
}
