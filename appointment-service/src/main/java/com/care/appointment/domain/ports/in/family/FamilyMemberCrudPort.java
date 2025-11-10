package com.care.appointment.domain.ports.in.family;

import com.care.appointment.domain.model.FamilyMember;
import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberCrudPort {
    FamilyMember save(FamilyMember domain);
    FamilyMember update(FamilyMember domain);
    Optional<FamilyMember> findById(UUID id);
    void deleteById(UUID id);
}

