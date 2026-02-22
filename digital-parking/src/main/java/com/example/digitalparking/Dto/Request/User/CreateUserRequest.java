package com.example.digitalparking.Dto.Request.User;

import com.example.digitalparking.Enum.Gender;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class CreateUserRequest {



        @NotBlank
        @Size(min = 5, max = 50)
        @Email
        private String email;

        @Size(min = 5, max = 120)
        private String password;
        @NotBlank

        @NotBlank
        @Size(min = 2, max = 25)
        private String firstName;

        private String userName;

        @NotBlank
        @Size(min = 2, max = 25)
        private String fatherName;

        private String grandFatherName;

        @NotNull(message = " gender can't be null")
        private Gender gender;

        @NotBlank
        @Size(min = 9, max = 13)
        private String mobilePhone;

        private Status userStatus;

        @NotNull
        private String roleUuid;


    }


