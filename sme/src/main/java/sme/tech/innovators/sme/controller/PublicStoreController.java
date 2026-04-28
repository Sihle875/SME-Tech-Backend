package sme.tech.innovators.sme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sme.tech.innovators.sme.dto.response.ApiResponse;
import sme.tech.innovators.sme.dto.response.PublicBusinessDto;
import sme.tech.innovators.sme.entity.Business;
import sme.tech.innovators.sme.exception.InvalidTokenException;
import sme.tech.innovators.sme.repository.BusinessRepository;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicStoreController {

    private final BusinessRepository businessRepository;

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
