package com.example.digitalparking.ServiceImpl.RoleServiceImpl;


import com.example.digitalparking.Dto.Request.Role.RoleRequest;
import com.example.digitalparking.Dto.Request.Role.RoleUpdateRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.PaginatedResponse.PaginatedResponse;
import com.example.digitalparking.Dto.Response.Role.RoleResponse;
import com.example.digitalparking.Entity.UserRole.Privilege;
import com.example.digitalparking.Entity.UserRole.Role;
import com.example.digitalparking.Repository.PrivilegeRepository;
import com.example.digitalparking.Repository.Role.RoleRepository;
import com.example.digitalparking.Service.RoleService;
import com.example.digitalparking.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PrivilegeRepository privilegeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }


    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {

        if (roleRepository.existsByRoleName(roleRequest.getRoleName())) {
            throw new BadRequestException("Role Name is already registered!");
        }

        var role = new Role();
        BeanUtils.copyProperties(roleRequest,role);

        role.setPrivileges(
                roleRequest.getPrivileges().stream()
                        .map(uuid -> {
                            Optional<Privilege> privilege = privilegeRepository.findByPrivilegeUuid(uuid);

                            return privilege.get();
                        }).collect(Collectors.toSet())
        );

        roleRepository.save(role);

        return mapToRoleResponse( roleRepository.save(role));
    }



    @Override
    public ResponseEntity<?> updateRole(String roleUuid, RoleUpdateRequest roleUpdateRequest) {

        return roleRepository.findByRoleUuid(roleUuid)
                .map(role1 -> {

                    role1.setRoleDescription(roleUpdateRequest.getRoleDescription());
                    role1.setRoleName(roleUpdateRequest.getRoleName());
                    role1.setPrivileges(
                            roleUpdateRequest.getPrivileges().stream()
                                    .map(uuid -> {
                                        Optional<Privilege> privilege = privilegeRepository.findByPrivilegeUuid(uuid);
                                        return privilege.get();
                                    }).collect(Collectors.toSet())
                    );

                    roleRepository.save(role1);
                    return ResponseEntity.ok(new MessageResponse("Role updated successfully!"));
                })
                .orElseThrow(() -> new BadRequestException("Role not found!"));
    }

    @Override
    public ResponseEntity<?> deleteRole(String roleUuid) {
        return roleRepository.findByRoleUuid(roleUuid)
                .map(role1 -> {
                    roleRepository.delete(role1);

                    return ResponseEntity.ok("Role deleted Successfully!");
                })
                .orElseThrow(() -> new BadRequestException("Role not Found"));
    }

    @Override
    public RoleResponse getRole(String roleUuid) {
        return roleRepository.findByRoleUuid(roleUuid)
                .map(this::mapToRoleResponse)
                .orElseThrow(() -> new BadRequestException("Role Not Found"));
    }

    @Override
    public PaginatedResponse<RoleResponse> findAllRoles(String search, Pageable pageable) {
        return search != null ? getAllRolesWithSearch(search,pageable) :
                getAllRoles(pageable);
    }

    private PaginatedResponse<RoleResponse> getAllRoles(Pageable pageable) {

        Page<Role> roles = roleRepository.findAll(pageable);

        List<RoleResponse> roleResponses = roles.stream().
                map(this::mapToRoleResponse)
                .toList();

        return new PaginatedResponse<>(
                roleResponses,
                roles.getTotalElements(),
                roles.getTotalPages(),
                roles.getNumber(),
                roles.getSize()
        );
    }

    private PaginatedResponse<RoleResponse> getAllRolesWithSearch(String search, Pageable pageable) {
        Page<Role> roles = roleRepository.findAllByRoleNameContaining(search,pageable);

        List<RoleResponse> roleResponses = roles.stream().
                map(this::mapToRoleResponse)
                .toList();

        return new PaginatedResponse<>(
                roleResponses,
                roles.getTotalElements(),
                roles.getTotalPages(),
                roles.getNumber(),
                roles.getSize()
        );
    }


    private RoleResponse mapToRoleResponse(Role role){
        var response = new RoleResponse();
        BeanUtils.copyProperties(role, response);
        response.setPrivileges(
                role.getPrivileges().stream()
                        .map(Privilege::getPrivilegeUuid).toList()
        );

        return response;
    }

}


