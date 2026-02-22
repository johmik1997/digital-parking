package com.example.digitalparking.Repository.Role;


import com.example.digitalparking.Entity.UserRole.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleUuid(String roleUuid);

    Optional<Role> findByRoleName(String name);

    Page<Role> findAllByRoleNameContaining(String search, Pageable pageable);
}
