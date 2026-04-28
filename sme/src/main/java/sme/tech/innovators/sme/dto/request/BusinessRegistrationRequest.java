package sme.tech.innovators.sme.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BusinessRegistrationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;

    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 2)
    private String businessName;

    private String description;
}
