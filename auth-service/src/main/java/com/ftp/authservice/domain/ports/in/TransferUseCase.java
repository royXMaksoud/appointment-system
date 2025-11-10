package com.ftp.authservice.domain.ports.in;

import com.ftp.authservice.application.command.TransferCommand;
import com.ftp.authservice.domain.model.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferUseCase {
    Transfer executeTransfer(TransferCommand command);
    Optional<Transfer> getTransferById(UUID transferId);
    List<Transfer> getTransfersByUserId(UUID userId);
    List<Transfer> getAllTransfers();
} 