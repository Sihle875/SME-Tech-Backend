package sme.tech.innovators.sme.dto.response;

import lombok.Builder;
import lombok.Data;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AccountProfileDto {

    // User fields
    private UUID userId;
    private String email;
    private String fullName;
    private AccountStatus accountStatus;
    private UserRole role;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userUpdatedAt;

    // Business fields
    private UUID businessId;
    private String businessName;
    private String businessDescription;
    private String slug;
    private String publicLink;
    private LocalDateTime businessUpdatedAt;
}
