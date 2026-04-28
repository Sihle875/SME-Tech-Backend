package sme.tech.innovators.sme.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sme.tech.innovators.sme.exception.SlugGenerationException;
import sme.tech.innovators.sme.repository.BusinessRepository;

import java.util.List;

@Slf4j
@Service
public class SlugGeneratorService {

    private final BusinessRepository businessRepository;

    @Value("${app.slug.max-retries:5}")
    private int maxRetries;

    @Value("${app.slug.min-length:3}")
    private int minLength;

    @Value("${app.slug.max-length:50}")
    private int maxLength;

    @Value("#{'${app.slug.reserved-keywords}'.split(',')}")
    private List<String> reservedKeywords;

    public SlugGeneratorService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public String sanitizeSlug(String input) {
        if (input == null) return "";
        String slug = input.trim().toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\-]", "-");
        slug = slug.replaceAll("-{2,}", "-");
        slug = slug.replaceAll("^-|-$", "");
        if (slug.length() > maxLength) {
            slug = slug.substring(0, maxLength);
            slug = slug.replaceAll("-$", "");
        }
        return slug;
    }

    public String generateUniqueSlug(String businessName) {
        String base = sanitizeSlug(businessName);

        if (base.length() < minLength) {
            throw new SlugGenerationException("Business name too short to generate a valid slug");
        }

        if (reservedKeywords.contains(base)) {
            base = base + "-biz";
        }

        String candidate = base;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                if (!businessRepository.existsBySlugAndIsDeletedFalse(candidate)) {
                    return candidate;
                }
                candidate = base + "-" + i;
            } catch (DataIntegrityViolationException ex) {
                log.warn("Slug conflict on attempt {}: {}", i, candidate);
                candidate = base + "-" + i;
            }
        }
        throw new SlugGenerationException("Failed to generate unique slug after " + maxRetries + " retries");
    }

    public boolean isReservedKeyword(String slug) {
        return reservedKeywords.contains(slug);
    }

    public List<String> getReservedKeywords() {
        return reservedKeywords;
    }
}
