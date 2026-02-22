package com.example.digitalparking.Dto.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditProfileRequest {

    @Size(min = 5, max = 50)
    @Email
    private String email;

    @Size(min = 9, max = 13)
    private String mobilePhone;

    @Size(min = 2, max = 25)
    private String fatherName;
    private MultipartFile profilePicture;

    @Size(min = 2, max = 25)
    private String firstName;

    private String title;
}
