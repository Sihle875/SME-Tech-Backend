package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

public class PublicLinkPropertyTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Property
    void publicLinkMatchesExpectedFormat(@ForAll("validSlugs") String slug) {
        String publicLink = BASE_URL + "/store/" + slug;
        Assertions.assertTrue(publicLink.startsWith(BASE_URL + "/store/"),
                "Public link format invalid: " + publicLink);
        Assertions.assertTrue(publicLink.endsWith(slug),
                "Public link does not end with slug");
        Assertions.assertTrue(publicLink.matches("https?://[^/]+/store/[a-z0-9\\-]+"),
                "Public link does not match expected pattern");
    }

    @Provide
    Arbitrary<String> validSlugs() {
        return Arbitraries.of(
            "acme-coffee", "tech-shop", "my-store", "best-bakery",
            "quick-fix", "green-garden", "blue-ocean", "test-biz"
        );
    }
}
