package com.example.digitalparking.Dto.Request.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleUpdateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String roleName;
    @NotBlank
    @Size(max = 100)
    private String roleDescription;
    private List<String> privileges;
}
