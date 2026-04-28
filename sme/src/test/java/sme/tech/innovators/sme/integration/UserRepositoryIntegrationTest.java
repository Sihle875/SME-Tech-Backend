package sme.tech.innovators.sme.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.entity.UserRole;
import sme.tech.innovators.sme.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("hashed");
        user.setFullName("Test User");
        user.setAccountStatus(AccountStatus.VERIFIED);
        user.setRole(UserRole.OWNER);
        return user;
    }

    @Test
    void findByEmailExcludesSoftDeletedUsers() {
        User user = buildUser("test@example.com");
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        assertTrue(userRepository.findByEmailAndIsDeletedFalse("test@example.com").isEmpty());
    }

    @Test
    void existsByEmailReturnsFalseForDeletedUser() {
        User user = buildUser("deleted@example.com");
        user.setDeleted(true);
        userRepository.save(user);

        assertFalse(userRepository.existsByEmailAndIsDeletedFalse("deleted@example.com"));
    }

    @Test
    void uniqueConstraintOnEmailEnforced() {
        userRepository.save(buildUser("unique@example.com"));
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(buildUser("unique@example.com"));
        });
    }
}
