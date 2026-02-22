package com.example.digitalparking.Repository.Service;

import com.example.digitalparking.Entity.Service.ServiceRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceRateRepository extends JpaRepository<ServiceRate, Long> {

    Optional<ServiceRate> findTopByServiceIdOrderByEffectiveFromDesc(Long serviceId);

        @Query("SELECT r FROM ServiceRate r WHERE r.service.id = :serviceId ORDER BY r.effectiveFrom DESC")
        List<ServiceRate> findByServiceIdOrderByEffectiveFromDesc(@Param("serviceId") Long serviceId);

        @Query("SELECT r FROM ServiceRate r WHERE r.service.id = :serviceId AND r.effectiveFrom <= :date ORDER BY r.effectiveFrom DESC")
        List<ServiceRate> findRatesEffectiveOnOrBefore(@Param("serviceId") Long serviceId,
                                                       @Param("date") LocalDate date);

        @Query(value = "SELECT * FROM service_rates sr WHERE sr.service_id = :serviceId " +
                "AND sr.effective_from <= CURRENT_DATE ORDER BY sr.effective_from DESC LIMIT 1",
                nativeQuery = true)
        Optional<ServiceRate> findCurrentRateByServiceId(@Param("serviceId") Long serviceId);

        Optional<ServiceRate> findByRateUuid(String rateUuid);
    }


