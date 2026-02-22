package com.example.digitalparking.ServiceImpl.UpdateProfileImpl;

import com.example.digitalparking.Dto.Request.User.EditProfileRequest;
import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.Service.CloudinaryService;
import com.example.digitalparking.Service.UpdateProfileService;
import com.example.digitalparking.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@AllArgsConstructor
@Service
public class UpdateProfileImpl implements UpdateProfileService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ResponseEntity<?> updateProfile(EditProfileRequest editProfileRequest, String userUuid) {
        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmail(editProfileRequest.getEmail());
        user.setFirstName(editProfileRequest.getFirstName());
        user.setFatherName(editProfileRequest.getFatherName());
        user.setMobilePhone(editProfileRequest.getMobilePhone());
        user.setTitle(editProfileRequest.getTitle());

        MultipartFile file = editProfileRequest.getProfilePicture();
        if (file != null && !file.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(file);
                user.setProfilePicture(imageUrl); // Save Cloudinary public URL
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully with profile image");
    }
}
