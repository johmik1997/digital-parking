package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.Role.RoleRequest;
import com.example.digitalparking.Dto.Request.Role.RoleUpdateRequest;
import com.example.digitalparking.Dto.Response.PaginatedResponse.PaginatedResponse;
import com.example.digitalparking.Dto.Response.Role.RoleResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


public interface RoleService {
    RoleResponse createRole(RoleRequest roleRequest);
    ResponseEntity<?> updateRole(String roleUuid, @Valid RoleUpdateRequest roleUpdateRequest);
    ResponseEntity<?> deleteRole(String roleString);
    RoleResponse getRole(String roleUuid);
    PaginatedResponse<RoleResponse> findAllRoles(String search, Pageable pageable);
}