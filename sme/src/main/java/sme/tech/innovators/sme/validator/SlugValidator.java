package sme.tech.innovators.sme.validator;

import org.springframework.stereotype.Component;
import sme.tech.innovators.sme.exception.InvalidSlugException;

import java.util.List;

@Component
public class SlugValidator {

    private static final List<String> RESERVED_KEYWORDS = List.of(
            "admin", "api", "app", "auth", "dashboard", "login", "logout",
            "register", "signup", "store", "support", "www", "mail",
            "static", "assets", "public", "private", "internal", "system", "root"
    );

    public void validateSlug(String slug) {
        if (slug == null || slug.length() < 3) {
            throw new InvalidSlugException("Slug must be at least 3 characters long");
        }
        if (slug.length() > 50) {
            throw new InvalidSlugException("Slug must be at most 50 characters long");
        }
        if (!slug.matches("[a-z0-9-]+")) {
            throw new InvalidSlugException("Slug may only contain lowercase letters, numbers, and hyphens");
        }
        if (RESERVED_KEYWORDS.contains(slug)) {
            throw new InvalidSlugException("Slug '" + slug + "' is a reserved keyword and cannot be used");
        }
    }
}
