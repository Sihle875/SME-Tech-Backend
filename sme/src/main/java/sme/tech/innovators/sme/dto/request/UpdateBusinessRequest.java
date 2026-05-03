package sme.tech.innovators.sme.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBusinessRequest {

    // Null means "do not change"
    @Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    private String name;

    // Null means "do not change"
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}
