package sme.tech.innovators.sme.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BusinessDto {
    private UUID id;
    private String name;
    private String slug;
    private String publicLink;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
