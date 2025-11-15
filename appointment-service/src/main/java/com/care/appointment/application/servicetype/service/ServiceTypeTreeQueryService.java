package com.care.appointment.application.servicetype.service;

import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import com.care.appointment.infrastructure.db.repositories.ServiceTypeRepository;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeTreeNodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServiceTypeTreeQueryService {

    private final ServiceTypeRepository serviceTypeRepository;

    public List<ServiceTypeTreeNodeDTO> getTree() {
        List<ServiceTypeEntity> entities = serviceTypeRepository.findAllActive();
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, List<ServiceTypeEntity>> byParent = new HashMap<>();
        for (ServiceTypeEntity entity : entities) {
            UUID parentId = entity != null ? entity.getParentServiceTypeId() : null;
            List<ServiceTypeEntity> bucket = byParent.get(parentId);
            if (bucket == null) {
                bucket = new ArrayList<>();
                byParent.put(parentId, bucket);
            }
            bucket.add(entity);
        }

        return buildTree(null, byParent);
    }

    private List<ServiceTypeTreeNodeDTO> buildTree(UUID parentId,
                                                   Map<UUID, List<ServiceTypeEntity>> byParent) {
        List<ServiceTypeEntity> children = byParent.getOrDefault(parentId, Collections.emptyList());

        return children.stream()
                .sorted(Comparator
                        .comparing(ServiceTypeEntity::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ServiceTypeEntity::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(entity -> ServiceTypeTreeNodeDTO.builder()
                        .serviceTypeId(entity.getServiceTypeId())
                        .parentId(entity.getParentServiceTypeId())
                        .name(entity.getName())
                        .code(entity.getCode())
                        .leaf(Boolean.TRUE.equals(entity.getIsLeaf()))
                        .displayOrder(entity.getDisplayOrder())
                        .children(buildTree(entity.getServiceTypeId(), byParent))
                        .build())
                .collect(Collectors.toList());
    }
}



