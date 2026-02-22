package com.example.digitalparking.Controller;


import Utils.Pagination.PaginationUtils;
import com.example.digitalparking.Dto.Request.Role.RoleRequest;
import com.example.digitalparking.Dto.Request.Role.RoleUpdateRequest;
import com.example.digitalparking.Dto.Response.PaginatedResponse.PaginatedResponse;
import com.example.digitalparking.Dto.Response.Role.RoleResponse;
import com.example.digitalparking.Service.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    // @PreAuthorize("hasRole('ROEL_create-role')")
    public RoleResponse createRole(@Valid @RequestBody RoleRequest roleRequest) {
        return roleService.createRole(roleRequest);
    }

    @PutMapping(path="/{roleUuid}")
    //@PreAuthorize("hasRole('ROLE_update_role')")
    public ResponseEntity<?> updateRole(@PathVariable String roleUuid, @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        return roleService.updateRole(roleUuid, roleUpdateRequest);
    }

    @GetMapping(path="/{roleUuid}")
    //@PreAuthorize("hasRole('ROLE_read_role')")
    public RoleResponse getRole(@PathVariable String roleUuid) {
        return roleService.getRole(roleUuid);
    }

    @GetMapping("/getAll")
    //@PreAuthorize("hasRole('ROLE_read_role')")
    public PaginatedResponse<RoleResponse> findAllRoles(
            @RequestParam (value = "search" , required = false) String search,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        Pageable pageable = PaginationUtils.paginateResource(page,limit,sortBy,sortDirection);
        return roleService.findAllRoles(search,pageable);
    }

    @DeleteMapping(path="/{roleUuid}")
    //@PreAuthorize("hasRole('ROLE_delete_role')")
    public ResponseEntity<?> deleteRole(@PathVariable String roleUuid) {
        return roleService.deleteRole(roleUuid);
    }
}
