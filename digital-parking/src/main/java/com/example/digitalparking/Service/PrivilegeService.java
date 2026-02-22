package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.Role.PrivilegeRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeMyResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeResponse;
import org.springframework.http.ResponseEntity;



public interface PrivilegeService {
    PrivilegeResponse createPrivilege(PrivilegeRequest privilegeRequest);
    ResponseEntity<MessageResponse> updatePrivilege(String privilegeUuid, PrivilegeRequest privilegeRequest);
    ResponseEntity<?> deletePrivilege(String privilegeUuid);
    PrivilegeResponse getPrivilege(String privilegeUuid);
    PrivilegeMyResponse getPrivileges(int page, int limit, String searchKey);

}