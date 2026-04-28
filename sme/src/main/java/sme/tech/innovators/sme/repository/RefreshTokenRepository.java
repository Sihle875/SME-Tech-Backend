package sme.tech.innovators.sme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sme.tech.innovators.sme.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUserId(UUID userId);
    void deleteAllByUserId(UUID userId);
}
