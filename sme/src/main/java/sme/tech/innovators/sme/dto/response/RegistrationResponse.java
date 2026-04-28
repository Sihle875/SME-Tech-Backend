package sme.tech.innovators.sme.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegistrationResponse {

    private UUID userId;
    private UUID businessId;
    private String publicLink;
    private String message;
}
