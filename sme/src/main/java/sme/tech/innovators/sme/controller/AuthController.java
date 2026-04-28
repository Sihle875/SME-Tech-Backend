package sme.tech.innovators.sme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sme.tech.innovators.sme.dto.request.LoginRequest;
import sme.tech.innovators.sme.dto.request.RefreshTokenRequest;
import sme.tech.innovators.sme.dto.request.RegistrationRequest;
import sme.tech.innovators.sme.dto.response.ApiResponse;
import sme.tech.innovators.sme.dto.response.AuthResponse;
import sme.tech.innovators.sme.dto.response.RegistrationResponse;
import sme.tech.innovators.sme.service.AuthService;
import sme.tech.innovators.sme.service.RegistrationService;
import sme.tech.innovators.sme.service.VerificationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final VerificationService verificationService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegistrationResponse>> register(
            @Valid @RequestBody RegistrationRequest request,
            HttpServletRequest httpRequest) {
        String ip = extractIp(httpRequest);
        RegistrationResponse response = registrationService.registerUserAndBusiness(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam String token) {
        verificationService.verifyToken(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestParam String email) {
        verificationService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Verification email resent"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
