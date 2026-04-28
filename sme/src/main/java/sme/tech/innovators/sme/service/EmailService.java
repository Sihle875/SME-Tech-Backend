package sme.tech.innovators.sme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuditService auditService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        String verificationLink = baseUrl + "/api/v1/auth/verify?token=" + token;
        String subject = "Verify your email address";
        String body = "Hi " + fullName + ",\n\nPlease verify your email by clicking the link below:\n\n"
                + verificationLink + "\n\nThis link expires in 24 hours.\n\nThank you!";
        sendWithRetry(toEmail, subject, body, "VERIFICATION_EMAIL");
    }

    @Async("emailTaskExecutor")
    public void sendWelcomeEmail(String toEmail, String fullName, String publicLink) {
        String subject = "Welcome to SME Operations!";
        String body = "Hi " + fullName + ",\n\nYour business is now live!\n\nPublic link: " + publicLink
                + "\n\nThank you for joining us!";
        sendWithRetry(toEmail, subject, body, "WELCOME_EMAIL");
    }

    private void sendWithRetry(String toEmail, String subject, String body, String emailType) {
        int maxRetries = 3;
        long[] backoffMs = {1000, 2000, 4000};
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                return;
            } catch (Exception e) {
                log.warn("Email send attempt {} failed for {}: {}", attempt + 1, toEmail, e.getMessage());
                auditService.logSecurityEvent(emailType + "_FAILURE", "system", toEmail,
                        "Attempt " + (attempt + 1) + ": " + e.getMessage());
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(backoffMs[attempt]);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("All {} email send attempts failed for {}", maxRetries, toEmail);
    }
}
