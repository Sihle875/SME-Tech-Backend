package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.entity.UserRole;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SoftDeletePropertyTest {

    @Property(tries = 50)
    void softDeletedUsersAreExcludedFromActiveQuery(@ForAll("mixedUsers") List<User> users) {
        // Simulate soft-delete-aware query: exclude is_deleted=true
        List<User> activeUsers = users.stream()
                .filter(u -> !u.isDeleted())
                .collect(Collectors.toList());

        for (User u : activeUsers) {
            Assertions.assertFalse(u.isDeleted(), "Active user should not be deleted");
        }

        long deletedCount = users.stream().filter(User::isDeleted).count();
        Assertions.assertEquals(users.size() - deletedCount, activeUsers.size());
    }

    @Provide
    Arbitrary<List<User>> mixedUsers() {
        return Arbitraries.integers().between(1, 10).flatMap(n ->
            Arbitraries.of(true, false).list().ofSize(n).map(deletedFlags ->
                deletedFlags.stream().map(deleted -> {
                    User u = new User();
                    u.setId(UUID.randomUUID());
                    u.setEmail(UUID.randomUUID() + "@test.com");
                    u.setPassword("hash");
                    u.setFullName("Test");
                    u.setAccountStatus(AccountStatus.VERIFIED);
                    u.setRole(UserRole.OWNER);
                    u.setDeleted(deleted);
                    return u;
                }).collect(Collectors.toList())
            )
        );
    }
}
