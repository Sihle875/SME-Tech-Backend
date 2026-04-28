package sme.tech.innovators.sme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import sme.tech.innovators.sme.entity.AuditLog;
import sme.tech.innovators.sme.repository.AuditLogRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logRegistrationAttempt(String ipAddress, String email) {
        save(ipAddress, email, "REGISTRATION_ATTEMPT", "PENDING", null, null, null);
    }

    public void logRegistrationSuccess(UUID userId, UUID businessId, String email, String ipAddress) {
        save(ipAddress, email, "REGISTRATION_SUCCESS", "SUCCESS", userId, businessId, null);
    }

    public void logRegistrationFailure(String email, String ipAddress, String reason, String details) {
        save(ipAddress, email, "REGISTRATION_FAILURE", "FAILURE", null, null, details);
    }

    public void logVerificationAttempt(String token, String ipAddress, boolean success, String details) {
        String outcome = success ? "SUCCESS" : "FAILURE";
        save(ipAddress, null, "VERIFICATION_ATTEMPT", outcome, null, null, details);
    }

    public void logRateLimitViolation(String ipAddress, String email, String attemptType) {
        save(ipAddress, email, "RATE_LIMIT_EXCEEDED", "BLOCKED", null, null, "attemptType=" + attemptType);
    }

    public void logSecurityEvent(String eventType, String ipAddress, String email, String details) {
        save(ipAddress, email, eventType, "FAILURE", null, null, details);
    }

    private void save(String ipAddress, String email, String eventType, String outcome,
                      UUID userId, UUID businessId, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .ipAddress(ipAddress != null ? ipAddress : "unknown")
                    .email(email)
                    .eventType(eventType)
                    .outcome(outcome)
                    .userId(userId)
                    .businessId(businessId)
                    .details(details)
                    .correlationId(MDC.get("requestId"))
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Never let audit logging break the main flow
        }
    }
}
