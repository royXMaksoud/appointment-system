package com.ftp.authservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transfer {
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amount;
    private String description;
    private TransferStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Transfer(UUID id, UUID fromUserId, UUID toUserId, BigDecimal amount, 
                   String description, TransferStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getFromUserId() { return fromUserId; }
    public UUID getToUserId() { return toUserId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public TransferStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setFromUserId(UUID fromUserId) { this.fromUserId = fromUserId; }
    public void setToUserId(UUID toUserId) { this.toUserId = toUserId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TransferStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
} 