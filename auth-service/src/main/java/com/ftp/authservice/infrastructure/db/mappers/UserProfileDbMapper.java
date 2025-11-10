package com.ftp.authservice.infrastructure.db.mappers;


import com.ftp.authservice.domain.model.UserProfile;
import com.ftp.authservice.infrastructure.db.entities.UserProfileEntity;

public class UserProfileDbMapper {

    public static UserProfile toDomain(UserProfileEntity e) {
        if (e == null) return null;
        return UserProfile.builder()
                .userProfileId(e.getUserProfileId())
                .userId(e.getUserId())
                .tenantId(e.getTenantId())
                .companyId(e.getCompanyId())
                .countryId(e.getCountryId())
                .cityId(e.getCityId())
                .preferredLanguage(e.getPreferredLanguage())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .rowVersion(e.getRowVersion())
                .build();
    }

    public static UserProfileEntity toEntity(UserProfile d) {
        if (d == null) return null;
        return UserProfileEntity.builder()
                .userProfileId(d.getUserProfileId())
                .userId(d.getUserId())
                .tenantId(d.getTenantId())
                .companyId(d.getCompanyId())
                .countryId(d.getCountryId())
                .cityId(d.getCityId())
                .preferredLanguage(d.getPreferredLanguage())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .rowVersion(d.getRowVersion())
                .build();
    }

    public static void copyToEntity(UserProfile d, UserProfileEntity e) {
        e.setTenantId(d.getTenantId());
        e.setCompanyId(d.getCompanyId());
        e.setCountryId(d.getCountryId());
        e.setCityId(d.getCityId());
        e.setPreferredLanguage(d.getPreferredLanguage());
        // createdAt/updatedAt تُدار بـ @PrePersist/@PreUpdate
    }
}
