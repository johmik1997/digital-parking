package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.User.EditProfileRequest;
import org.springframework.http.ResponseEntity;
import java.lang.String;

public interface UpdateProfileService {
    ResponseEntity<?> updateProfile(EditProfileRequest editProfileRequest, String userUuid);
}