package sme.tech.innovators.sme.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sme.tech.innovators.sme.entity.*;
import sme.tech.innovators.sme.repository.BusinessRepository;
import sme.tech.innovators.sme.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BusinessRepositoryIntegrationTest {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("hashed");
        user.setFullName("Test");
        user.setAccountStatus(AccountStatus.VERIFIED);
        user.setRole(UserRole.OWNER);
        return userRepository.save(user);
    }

    private Business buildBusiness(String slug, User owner) {
        Business b = new Business();
        b.setName("Test Business");
        b.setSlug(slug);
        b.setPublicLink("http://localhost/store/" + slug);
        b.setOwner(owner);
        return b;
    }

    @Test
    void findBySlugExcludesSoftDeletedBusinesses() {
        User owner = savedUser("owner1@test.com");
        Business b = buildBusiness("soft-deleted-biz", owner);
        b.setDeleted(true);
        b.setDeletedAt(LocalDateTime.now());
        businessRepository.save(b);

        assertTrue(businessRepository.findBySlugAndIsDeletedFalse("soft-deleted-biz").isEmpty());
    }

    @Test
    void uniqueConstraintOnSlugEnforced() {
        User owner1 = savedUser("owner2@test.com");
        User owner2 = savedUser("owner3@test.com");
        businessRepository.save(buildBusiness("duplicate-slug", owner1));

        assertThrows(Exception.class, () -> {
            businessRepository.saveAndFlush(buildBusiness("duplicate-slug", owner2));
        });
    }
}
