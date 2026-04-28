package sme.tech.innovators.sme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sme.tech.innovators.sme.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByIpAddress(String ipAddress);
    List<AuditLog> findByEmail(String email);
    List<AuditLog> findByEventType(String eventType);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByCorrelationId(String correlationId);
}
