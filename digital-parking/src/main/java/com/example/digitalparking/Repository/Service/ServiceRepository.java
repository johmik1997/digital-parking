package com.example.digitalparking.Repository.Service;


import com.example.digitalparking.Entity.Service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByActiveTrue();
    Optional<ServiceEntity> findByServiceUUid(String serviceUUid);
    long countByActiveTrue();

    @Query("select coalesce(sum(s.slot), 0) from ServiceEntity s where s.active = true")
    Long sumActiveSlots();
}
