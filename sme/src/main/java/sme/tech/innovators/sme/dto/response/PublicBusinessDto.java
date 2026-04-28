package sme.tech.innovators.sme.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicBusinessDto {
    private String name;
    private String slug;
    private String description;
    private String publicLink;
}
