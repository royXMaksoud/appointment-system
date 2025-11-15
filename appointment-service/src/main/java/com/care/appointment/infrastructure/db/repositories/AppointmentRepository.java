package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends 
        JpaRepository<AppointmentEntity, UUID>,
        JpaSpecificationExecutor<AppointmentEntity> {
    
    List<AppointmentEntity> findByBeneficiaryIdOrderByAppointmentDateDescAppointmentTimeDesc(UUID beneficiaryId);
    
    Page<AppointmentEntity> findByBeneficiaryId(UUID beneficiaryId, Pageable pageable);
    
    List<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDate(
        UUID organizationBranchId, LocalDate appointmentDate);
    
    List<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDateBetween(
        UUID organizationBranchId, LocalDate startDate, LocalDate endDate);
    
    List<AppointmentEntity> findByAppointmentStatusId(UUID appointmentStatusId);
    
    Optional<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
        UUID organizationBranchId, LocalDate appointmentDate, LocalTime appointmentTime);
    
    boolean existsByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
        UUID organizationBranchId, LocalDate appointmentDate, LocalTime appointmentTime);
    
    @Query("SELECT COUNT(a) FROM AppointmentEntity a WHERE a.organizationBranchId = :branchId " +
           "AND a.appointmentDate = :date")
    long countByBranchAndDate(@Param("branchId") UUID branchId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM AppointmentEntity a WHERE a.organizationBranchId = :branchId " +
           "AND a.appointmentDate = :date AND a.cancelledAt IS NULL")
    long countActiveByBranchAndDate(@Param("branchId") UUID branchId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDate = :date " +
           "AND a.appointmentStatusId IN :statusIds ORDER BY a.appointmentTime ASC")
    List<AppointmentEntity> findByDateAndStatuses(
        @Param("date") LocalDate date, @Param("statusIds") List<UUID> statusIds);
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.appointmentDate >= :fromDate ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<AppointmentEntity> findUpcomingAppointments(
        @Param("beneficiaryId") UUID beneficiaryId, @Param("fromDate") LocalDate fromDate);
    
    /**
     * Check if beneficiary has an appointment for same service type and date
     * Prevents duplicate bookings for same person, same service, same day
     */
    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.serviceTypeId = :serviceTypeId AND a.appointmentDate = :appointmentDate " +
           "AND a.appointmentStatusId NOT IN :excludedStatusIds")
    boolean hasExistingAppointmentForServiceAndDate(
        @Param("beneficiaryId") UUID beneficiaryId,
        @Param("serviceTypeId") UUID serviceTypeId,
        @Param("appointmentDate") LocalDate appointmentDate,
        @Param("excludedStatusIds") List<UUID> excludedStatusIds);
    
    /**
     * Check if beneficiary has an appointment for same service type with status NOT in excluded list
     * Prevents booking same service type multiple times unless previous appointments are CLOSED/CANCELLED
     */
    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.serviceTypeId = :serviceTypeId " +
           "AND a.appointmentStatusId NOT IN :excludedStatusIds")
    boolean hasActiveAppointmentForService(
        @Param("beneficiaryId") UUID beneficiaryId,
        @Param("serviceTypeId") UUID serviceTypeId,
        @Param("excludedStatusIds") List<UUID> excludedStatusIds);

    /**
     * Find appointment by unique appointment code (e.g., HQ-2025-0001)
     */
    Optional<AppointmentEntity> findByAppointmentCode(String appointmentCode);

    /**
     * Query appointments for dashboard with aggregation support
     * Returns appointment views with center and beneficiary data
     */

    @Query(value = """
            SELECT
                a.appointment_id as id,
                a.organization_branch_id as centerId,
                NULL as centerName,
                NULL as governorate,
                NULL::double precision as latitude,
                NULL::double precision as longitude,
                a.appointment_date as appointmentDate,
                COALESCE(ast.code, 'UNKNOWN') as status,
                st.name as serviceTypeName,
                a.priority,
                'Unknown' as gender,
                b.date_of_birth as dateOfBirth
            FROM appointments a
            LEFT JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
            LEFT JOIN service_types st ON a.service_type_id = st.service_type_id
            LEFT JOIN appointment_statuses ast ON a.appointment_status_id = ast.appointment_status_id
            WHERE a.appointment_date BETWEEN :dateFrom AND :dateTo
            AND (:serviceTypeFilterDisabled = true OR a.service_type_id IN (:serviceTypeIds))
            AND (:statusFilterDisabled = true OR ast.code IN (:statuses))
            AND (:centerFilterDisabled = true OR a.organization_branch_id IN (:centerIds))
            AND (:priority IS NULL OR a.priority = :priority)
            AND (:beneficiaryStatus IS NULL OR b.is_active = :beneficiaryStatus)
            """, nativeQuery = true)
    List<?> findAppointmentsForDashboard(
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("serviceTypeFilterDisabled") boolean serviceTypeFilterDisabled,
        @Param("serviceTypeIds") List<UUID> serviceTypeIds,
        @Param("statusFilterDisabled") boolean statusFilterDisabled,
        @Param("statuses") List<String> statuses,
        @Param("centerFilterDisabled") boolean centerFilterDisabled,
        @Param("centerIds") List<UUID> centerIds,
        @Param("priority") String priority,
        @Param("beneficiaryStatus") Boolean beneficiaryStatus
    );
}
