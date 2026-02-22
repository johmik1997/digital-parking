package com.example.digitalparking.Dto.Request.User;


import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRequest {

    @NotBlank
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 25)
    private String title;

    @NotBlank
    @Size(min = 2, max = 25)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String fatherName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String grandFatherName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String userName;

    @NotBlank
    @Size(min = 1, max = 10)
    private String gender;

    @NotBlank
    @Size(min = 9, max = 13)
    private String mobilePhone;

    private Status userStatus;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @NotNull
    private String roleUuid;

    private String branchId;

}
