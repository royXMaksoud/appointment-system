package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)

    private UserJpaEntity user;



    @Column(nullable = false)
    private Instant expiryDate;

    public UUID getUserId() {
        return user.getId();
    }

    public boolean isValid() {
        return this.expiryDate != null && this.expiryDate.isAfter(Instant.now());
    }


}
