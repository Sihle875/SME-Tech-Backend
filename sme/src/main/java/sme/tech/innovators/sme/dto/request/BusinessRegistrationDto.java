package sme.tech.innovators.sme.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusinessRegistrationDto {

    @NotBlank
    @Size(min = 2)
    private String name;

    private String description;
}
