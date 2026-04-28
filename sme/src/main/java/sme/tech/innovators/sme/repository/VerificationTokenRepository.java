package sme.tech.innovators.sme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.entity.VerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}
