package com.example.digitalparking.Dto.Response.User;

import com.example.digitalparking.Enum.Gender;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String userUuid;
    private String email;
    private String roleUuid;
    private String roleName;
    private String title;
    private String firstName;
    private String fatherName;
    private String grandFatherName;
    private Gender gender;
    private String mobilePhone;
    private Status userStatus;
    private UserType userType;
    private String profilePicture;
    private List<String> privileges;

    public JwtResponse(String accessToken, String userUuid, String email, String roleUuid, String roleName, String title, String firstName, String fatherName,
                       String grandFatherName, Gender gender, String mobilePhone, Status userStatus, UserType userType,
                       String profilePicture, List<String> privileges) {
        this.token = accessToken;
        this.userUuid = userUuid;
        this.email = email;
        this.roleUuid = roleUuid;
        this.roleName=roleName;
        this.title=title;
        this.firstName=firstName;
        this.fatherName=fatherName;
        this.grandFatherName=grandFatherName;
        this.gender=gender;
        this.mobilePhone=mobilePhone;
        this.userStatus=userStatus;
        this.userType=userType;
        this.profilePicture=profilePicture;
        this.privileges = privileges;

    }
}
