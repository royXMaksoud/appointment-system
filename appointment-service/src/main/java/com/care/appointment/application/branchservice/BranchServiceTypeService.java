package com.care.appointment.application.branchservice;

import com.care.appointment.infrastructure.client.AccessManagementClient;
import com.care.appointment.infrastructure.db.entities.CenterServiceEntity;
import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import com.care.appointment.infrastructure.db.repositories.CenterServiceRepository;
import com.care.appointment.infrastructure.db.repositories.ServiceTypeRepository;
import com.care.appointment.web.dto.OrganizationBranchDTO;
import com.care.appointment.web.dto.OrganizationDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeAssignmentDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeDetailDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeNodeDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeSummaryDTO;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.ScopeCriteria;
import com.sharedlib.core.filter.ValueDataType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceTypeService {

    private final CenterServiceRepository centerServiceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AccessManagementClient accessManagementClient;

    @Transactional
    public void upsertAssignments(UUID branchId,
                                  List<BranchServiceTypeAssignmentDTO> assignments) {
        Objects.requireNonNull(branchId, "branchId is required");

        List<CenterServiceEntity> existing = centerServiceRepository.findByOrganizationBranchId(branchId);
        Map<UUID, CenterServiceEntity> existingByService = existing.stream()
            .collect(Collectors.toMap(CenterServiceEntity::getServiceTypeId, Function.identity()));

        Set<UUID> allowedServiceIds = serviceTypeRepository.findAllActive()
            .stream()
            .map(ServiceTypeEntity::getServiceTypeId)
            .collect(Collectors.toSet());

        Set<UUID> incomingIds = new HashSet<>();

        for (BranchServiceTypeAssignmentDTO assignment : assignments) {
            if (assignment.getServiceTypeId() == null) {
                continue;
            }
            if (!allowedServiceIds.contains(assignment.getServiceTypeId())) {
                throw new IllegalArgumentException("Invalid or inactive serviceTypeId: " + assignment.getServiceTypeId());
            }

            CenterServiceEntity entity = existingByService.get(assignment.getServiceTypeId());
            if (entity == null) {
                entity = CenterServiceEntity.builder()
                    .organizationBranchId(branchId)
                    .serviceTypeId(assignment.getServiceTypeId())
                    .build();
            }

            entity.setIsActive(Boolean.TRUE);
            entity.setCost(normalizeCost(assignment.getCost()));
            centerServiceRepository.save(entity);
            incomingIds.add(assignment.getServiceTypeId());
        }

        for (CenterServiceEntity entity : existing) {
            if (!incomingIds.contains(entity.getServiceTypeId()) && Boolean.TRUE.equals(entity.getIsActive())) {
                entity.setIsActive(Boolean.FALSE);
                centerServiceRepository.save(entity);
            }
        }
    }

    @Transactional
    public void replaceAssignments(UUID branchId,
                                   List<BranchServiceTypeAssignmentDTO> assignments) {
        upsertAssignments(branchId, assignments);
    }

    @Transactional
    public void replaceAssignments(UUID branchId,
                                   List<BranchServiceTypeAssignmentDTO> assignments,
                                   List<UUID> allowedBranchIds) {
        ensureBranchAllowed(branchId, allowedBranchIds);
        replaceAssignments(branchId, assignments);
    }

    @Transactional
    public BranchServiceTypeDetailDTO getBranchDetail(UUID branchId, List<UUID> allowedBranchIds) {
        ensureBranchAllowed(branchId, allowedBranchIds);

        OrganizationBranchDTO branch = accessManagementClient.getOrganizationBranch(branchId);
        if (branch == null) {
            throw new IllegalArgumentException("Branch not found: " + branchId);
        }

        Map<UUID, String> organizationNames = resolveOrganizationNames(List.of(branch));

        List<ServiceTypeEntity> serviceTypes = serviceTypeRepository.findAllActive();
        Map<UUID, List<ServiceTypeEntity>> byParent = new HashMap<>();
        for (ServiceTypeEntity serviceType : serviceTypes) {
            UUID parent = serviceType.getParentServiceTypeId();
            byParent.computeIfAbsent(parent, key -> new ArrayList<>()).add(serviceType);
        }

        List<CenterServiceEntity> assignments = centerServiceRepository.findByOrganizationBranchId(branchId);
        Map<UUID, CenterServiceEntity> assignmentsByService = assignments.stream()
            .filter(cs -> Boolean.TRUE.equals(cs.getIsActive()))
            .collect(Collectors.toMap(CenterServiceEntity::getServiceTypeId, Function.identity()));

        List<BranchServiceTypeNodeDTO> tree = buildTree(null, byParent, assignmentsByService);

        return BranchServiceTypeDetailDTO.builder()
            .organizationBranchId(branch.getOrganizationBranchId())
            .branchName(branch.getName())
            .organizationId(branch.getOrganizationId())
            .organizationName(organizationNames.get(branch.getOrganizationId()))
            .serviceTree(tree)
            .build();
    }

    @Transactional
    public List<BranchServiceTypeSummaryDTO> getBranchSummaries(List<UUID> allowedBranchIds) {
        if (allowedBranchIds == null || allowedBranchIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrganizationBranchDTO> branches = accessManagementClient.getBranchesByIds(allowedBranchIds);
        if (branches == null || branches.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, OrganizationBranchDTO> branchById = branches.stream()
            .collect(Collectors.toMap(OrganizationBranchDTO::getOrganizationBranchId, Function.identity()));

        List<UUID> branchIds = new ArrayList<>(branchById.keySet());
        Map<UUID, Long> activeCounts = branchIds.isEmpty()
            ? Collections.emptyMap()
            : centerServiceRepository.findByOrganizationBranchIdInAndIsActiveTrue(branchIds).stream()
                .collect(Collectors.groupingBy(
                    CenterServiceEntity::getOrganizationBranchId,
                    Collectors.counting()));

        Map<UUID, String> organizationNames = resolveOrganizationNames(branches);

        return branchById.values().stream()
            .sorted(Comparator.comparing(OrganizationBranchDTO::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
            .map(branch -> {
                return BranchServiceTypeSummaryDTO.builder()
                    .organizationBranchId(branch.getOrganizationBranchId())
                    .branchName(branch.getName())
                    .organizationId(branch.getOrganizationId())
                    .organizationName(organizationNames.get(branch.getOrganizationId()))
                    .assignedServiceCount(activeCounts.getOrDefault(branch.getOrganizationBranchId(), 0L))
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<BranchServiceTypeNodeDTO> buildTree(UUID parentId,
                                                     Map<UUID, List<ServiceTypeEntity>> byParent,
                                                     Map<UUID, CenterServiceEntity> assignmentsByService) {
        List<ServiceTypeEntity> children = byParent.getOrDefault(parentId, Collections.emptyList());

        return children.stream()
            .sorted(Comparator
                .comparing(ServiceTypeEntity::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(ServiceTypeEntity::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
            .map(child -> {
                CenterServiceEntity assignment = assignmentsByService.get(child.getServiceTypeId());
                BranchServiceTypeNodeDTO node = BranchServiceTypeNodeDTO.builder()
                    .serviceTypeId(child.getServiceTypeId())
                    .name(child.getName())
                    .code(child.getCode())
                    .parentServiceTypeId(child.getParentServiceTypeId())
                    .leaf(Boolean.TRUE.equals(child.getIsLeaf()))
                    .assigned(assignment != null)
                    .cost(assignment != null ? assignment.getCost() : BigDecimal.ZERO)
                    .displayOrder(child.getDisplayOrder())
                    .build();

                node.setChildren(buildTree(child.getServiceTypeId(), byParent, assignmentsByService));
                return node;
            })
            .collect(Collectors.toList());
    }

    private Map<UUID, String> resolveOrganizationNames(List<OrganizationBranchDTO> branches) {
        Set<UUID> branchIds = branches.stream()
            .map(OrganizationBranchDTO::getOrganizationBranchId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (branchIds.isEmpty()) {
            return Collections.emptyMap();
        }

        FilterRequest request = new FilterRequest();
        request.setScopes(List.of(ScopeCriteria.builder()
            .fieldName("organizationBranchId")
            .allowedValues(new ArrayList<>(branchIds))
            .dataType(ValueDataType.UUID)
            .build()));

        List<OrganizationDTO> organizations = accessManagementClient.getOrganizationsByBranchIds(request);
        if (organizations == null) {
            return Collections.emptyMap();
        }

        return organizations.stream()
            .filter(org -> org.getOrganizationId() != null)
            .collect(Collectors.toMap(
                OrganizationDTO::getOrganizationId,
                org -> Optional.ofNullable(org.getName()).orElse(""),
                (existing, ignored) -> existing));
    }

    private void ensureBranchAllowed(UUID branchId, List<UUID> allowedBranchIds) {
        if (allowedBranchIds == null || allowedBranchIds.isEmpty()) {
            return;
        }

        if (!allowedBranchIds.contains(branchId)) {
            throw new SecurityException("Branch not allowed for current user: " + branchId);
        }
    }

    private BigDecimal normalizeCost(BigDecimal cost) {
        if (cost == null) {
            return BigDecimal.ZERO;
        }
        return cost.max(BigDecimal.ZERO);
    }
}


