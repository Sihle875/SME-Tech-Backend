package sme.tech.innovators.sme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 50)
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column
    private UUID userId;

    @Column
    private UUID businessId;

    @Column(length = 36)
    private String correlationId;

    @PrePersist
    protected void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
