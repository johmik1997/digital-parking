package com.example.digitalparking.Repository;


import com.example.digitalparking.Entity.UserRole.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByPrivilegeName(String privilegeName);

    boolean existsByPrivilegeName(String privilegeName);

    Optional<Privilege> findByPrivilegeUuid(String privilegeString);

    List<Privilege> findAllByPrivilegeName(String privilegeName);
    List<Privilege> findAllByPrivilegeDescription(String privilegeDescription);

    Set<Privilege> findByPrivilegeUuidIn(Set<String> privilegeUuid);
}

