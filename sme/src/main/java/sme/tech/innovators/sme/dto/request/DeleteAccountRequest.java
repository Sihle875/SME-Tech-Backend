package sme.tech.innovators.sme.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {

    @NotBlank(message = "Password must not be blank")
    private String password;
}
