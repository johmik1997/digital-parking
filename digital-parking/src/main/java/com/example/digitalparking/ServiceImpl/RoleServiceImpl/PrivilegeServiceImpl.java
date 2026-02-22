package com.example.digitalparking.ServiceImpl.RoleServiceImpl;

import Utils.Pagination.PaginationUtils;
import com.example.digitalparking.Dto.Request.Role.PrivilegeRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeMyResponse;
import com.example.digitalparking.Dto.Response.Role.PrivilegeResponse;
import com.example.digitalparking.Entity.UserRole.Privilege;
import com.example.digitalparking.Repository.PrivilegeRepository;
import com.example.digitalparking.Service.PrivilegeService;
import com.example.digitalparking.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public PrivilegeResponse createPrivilege(PrivilegeRequest privilegeRequest) {

        if (privilegeRepository.existsByPrivilegeName(privilegeRequest.getPrivilegeName())) throw new BadRequestException("privilege is already registered!");

        Privilege privilege = new Privilege();
        BeanUtils.copyProperties(privilegeRequest, privilege);
        privilege.setPrivilegeName(privilege.getPrivilegeName().toLowerCase());

        return mapToPrivilegeResponse( privilegeRepository.save(privilege));
    }


    @Override
    public ResponseEntity<MessageResponse> updatePrivilege(String privilegeUuid, PrivilegeRequest privilegeRequest) {

        Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeUuid)
                .orElseThrow(() -> new BadRequestException("Privilege not found!"));

        BeanUtils.copyProperties(privilegeRequest,privilege);
        privilegeRepository.save(privilege);

        return ResponseEntity.ok(new MessageResponse("Privilege Updated successfully!"));
    }

    @Override
    public ResponseEntity<?> deletePrivilege(String privilegeString) {

        Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeString)
                .orElseThrow(() -> new BadRequestException("Privilege not found!"));

        privilegeRepository.delete(privilege);
        return ResponseEntity.ok(new MessageResponse("Privilege soft deleted permanently!"));
    }

    @Override
    public PrivilegeResponse getPrivilege(String privilegeUuid) {
        Privilege privilege  = privilegeRepository.findByPrivilegeUuid(privilegeUuid)
                .orElseThrow(() -> new BadRequestException("Privilege not found!"));

        PrivilegeResponse privilegeREsponse = new PrivilegeResponse();
        privilegeREsponse.setPrivilegeUuid(privilege.getPrivilegeUuid());
        privilegeREsponse.setPrivilegeName(privilege.getPrivilegeName());
        privilegeREsponse.setPrivilegeDescription(privilege.getPrivilegeDescription());
        return privilegeREsponse;
    }


    @Override
    public PrivilegeMyResponse getPrivileges(int page, int limit, String searchKey) {

        Pageable pageRequest = PaginationUtils.paginateResource(page,limit,"id","desc");
        Page<Privilege> privilegePage = privilegeRepository.findAll(pageRequest);
        long totalPages = privilegePage.getTotalPages();
        List<Privilege> privilegeList = privilegePage.getContent();

        PrivilegeMyResponse response = new PrivilegeMyResponse();
        response.setTotalPages(totalPages);
        List<PrivilegeResponse> privilegeResponseList = new ArrayList<>();
        for (Privilege privilege : privilegeList) {
            PrivilegeResponse privilegeResponse = new PrivilegeResponse();
            BeanUtils.copyProperties(privilege, privilegeResponse);
            privilegeResponseList.add(privilegeResponse);
        }

        response.setResponse(privilegeResponseList);
        return response;
    }

    private PrivilegeResponse mapToPrivilegeResponse(Privilege privilege){
        var response = new PrivilegeResponse();
        BeanUtils.copyProperties(privilege,response);
        return  response;
    }
}
