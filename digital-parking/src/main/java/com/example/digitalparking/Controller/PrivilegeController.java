package com.example.digitalparking.Controller;



import com.example.digitalparking.Dto.Request.Role.PrivilegeRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeMyResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeResponse;
import com.example.digitalparking.Service.PrivilegeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/users/privilege")
public class PrivilegeController {
    private final PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {

        this.privilegeService = privilegeService;
    }

    @PostMapping("/createPrivilege")
    //@PreAuthorize("hasRole('ROLE_create_privilege')")
    public PrivilegeResponse createPrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest) {
        return privilegeService.createPrivilege(privilegeRequest);
    }

    @GetMapping(path="/{privilegeUuid}")
    //@PreAuthorize("hasRole('ROLE_read_privilege')")
    public PrivilegeResponse getRole(@PathVariable String privilegeUuid) {
        return privilegeService.getPrivilege(privilegeUuid);
    }

    @GetMapping("/list")
    //@PreAuthorize("hasRole('ROLE_read_privilege')")
    public PrivilegeMyResponse getPrivileges(@RequestParam(name="search  ", required=false) String searchKey,
                                             @RequestParam(value="page", defaultValue = "1") int page,
                                             @RequestParam(value="limit", defaultValue = "25") int limit) {
        return privilegeService.getPrivileges(page, limit, searchKey);
    }

    @PutMapping(path="/{privilegeUuid}")
    //@PreAuthorize("hasRole('ROLE_update_privilege')")
    public ResponseEntity<MessageResponse> updatePrivilege(@PathVariable String privilegeUuid, @Valid @RequestBody PrivilegeRequest privilegeRequest) {
        return privilegeService.updatePrivilege(privilegeUuid, privilegeRequest);
    }

    @DeleteMapping(path="/{privilegeUuid}")
    //@PreAuthorize("hasRole('ROLE_delete_privilege')")
    public ResponseEntity<?> deletePrivilege(@PathVariable String privilegeUuid) {
        return privilegeService.deletePrivilege(privilegeUuid);
    }
}