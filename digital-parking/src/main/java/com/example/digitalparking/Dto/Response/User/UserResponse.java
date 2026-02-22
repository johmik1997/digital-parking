package com.example.digitalparking.Dto.Response.User;

import com.example.digitalparking.Enum.Gender;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;

@Getter
@Setter
public class UserResponse{

    private String userUuid;
    private String email;
    private String title;
    private String firstName;
    private String fatherName;
    private Gender Gender;
    private Instant createdAt;
    private String username;

    private String mobilePhone;
    private Status userStatus;
    private UserType userType;
    private String profilePicture;
    private boolean isDeleted;
    private String roleUuid;
    private String roleName;
    private long totalPages;
    long totalElements;
    int pageNumber;
}

