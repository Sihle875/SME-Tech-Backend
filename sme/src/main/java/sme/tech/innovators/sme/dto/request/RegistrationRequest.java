package sme.tech.innovators.sme.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotNull
    @Valid
    private BusinessRegistrationRequest business;
}
