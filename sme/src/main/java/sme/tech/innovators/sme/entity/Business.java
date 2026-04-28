package sme.tech.innovators.sme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 500)
    private String publicLink;

    @Column(length = 1000)
    private String description;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
