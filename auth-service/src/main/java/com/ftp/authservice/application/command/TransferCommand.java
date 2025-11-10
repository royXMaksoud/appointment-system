package com.ftp.authservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferCommand {
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amount;
    private String description;

    public TransferCommand(UUID fromUserId, UUID toUserId, BigDecimal amount, String description) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.description = description;
    }

    // Getters
    public UUID getFromUserId() { return fromUserId; }
    public UUID getToUserId() { return toUserId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }

    // Setters
    public void setFromUserId(UUID fromUserId) { this.fromUserId = fromUserId; }
    public void setToUserId(UUID toUserId) { this.toUserId = toUserId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
} 