package sme.tech.innovators.sme.dto.response;

import lombok.Builder;
import lombok.Data;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserWithBusinessDto {
    private UUID userId;
    private String email;
    private String fullName;
    private AccountStatus accountStatus;
    private UserRole role;
    private LocalDateTime userCreatedAt;
    private BusinessDto business;
}
