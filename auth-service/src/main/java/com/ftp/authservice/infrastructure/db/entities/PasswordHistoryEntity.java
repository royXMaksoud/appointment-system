package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "password_history",
        indexes = {
                @Index(name = "idx_password_history_user", columnList = "user_id"),
                @Index(name = "idx_password_history_changed_at", columnList = "changed_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "password_history_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Builder.Default
    @Column(name = "expired", nullable = false)
    private boolean expired = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onPersist() {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }
}
