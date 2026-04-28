package sme.tech.innovators.sme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sme.tech.innovators.sme.dto.request.LoginRequest;
import sme.tech.innovators.sme.dto.response.AuthResponse;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.RefreshToken;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.exception.AccountNotVerifiedException;
import sme.tech.innovators.sme.exception.InvalidTokenException;
import sme.tech.innovators.sme.exception.TokenRevokedException;
import sme.tech.innovators.sme.repository.RefreshTokenRepository;
import sme.tech.innovators.sme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (user.getAccountStatus() != AccountStatus.VERIFIED) {
            throw new AccountNotVerifiedException("Email verification required before login");
        }

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(900L)
                .build();
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new TokenRevokedException("Refresh token has been revoked");
        }
        if (refreshToken.isExpired()) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(900L)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
