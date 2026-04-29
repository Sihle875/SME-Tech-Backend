package sme.tech.innovators.sme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sme.tech.innovators.sme.dto.response.ApiResponse;
import sme.tech.innovators.sme.dto.response.PublicBusinessDto;
import sme.tech.innovators.sme.entity.Business;
import sme.tech.innovators.sme.exception.InvalidTokenException;
import sme.tech.innovators.sme.repository.BusinessRepository;

@Tag(name = "Public Store", description = "Public endpoints for accessing business storefront data — no authentication required")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicStoreController {

    private final BusinessRepository businessRepository;

    @Operation(summary = "Get business by slug",
               description = "Returns public business information (name, slug, description, publicLink). No authentication required. Sensitive fields such as owner email and internal IDs are excluded.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Business found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Business not found for the given slug")
    })
    @GetMapping("/store/{slug}")
    public ResponseEntity<ApiResponse<PublicBusinessDto>> getStore(@PathVariable String slug) {
        Business business = businessRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new InvalidTokenException("Business not found for slug: " + slug));

        PublicBusinessDto dto = PublicBusinessDto.builder()
                .name(business.getName())
                .slug(business.getSlug())
                .description(business.getDescription())
                .publicLink(business.getPublicLink())
                .build();

        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
