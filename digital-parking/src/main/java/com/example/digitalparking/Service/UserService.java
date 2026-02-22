package com.example.digitalparking.Service;


import com.example.digitalparking.Dto.Request.Password.ChangePasswordRequest;
import com.example.digitalparking.Dto.Request.Password.ForgotPassword;
import com.example.digitalparking.Dto.Request.Password.ResetPasswordRequest;
import com.example.digitalparking.Dto.Request.User.AuthRequest;
import com.example.digitalparking.Dto.Request.User.CreateUserRequest;
import com.example.digitalparking.Dto.Request.User.SignUpRequest;
import com.example.digitalparking.Dto.Request.User.UserRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.PaginatedResponse.PaginatedResponse;
import com.example.digitalparking.Dto.Response.User.UserResponse;
import io.jsonwebtoken.io.IOException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    ResponseEntity<?> AuthenticateUser(AuthRequest authRequest);

    ResponseEntity<?> Save(SignUpRequest signUpRequest);
    ResponseEntity<?> createUser(CreateUserRequest createUserRequest);
    ResponseEntity<MessageResponse> updateUser(String userUuid, UserRequest userRequest);
    ResponseEntity<?>forgotPassword(ForgotPassword forgotPassword);
    ResponseEntity<?> updateUserStatus(String userUuid);
    ResponseEntity<?> deleteUser(String userUuid);

    UserResponse getUser(String userUuid);

    ResponseEntity<?> sendPasswordResetCode(String email);

    PaginatedResponse<UserResponse> getUsers(String search, Pageable pageable);


    ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail);

    ResponseEntity<?> changePassword(ChangePasswordRequest resetPasswordDetail, String userUuid);

    ResponseEntity<?> verifyAccount(String emailVerificationToken);

    ResponseEntity<?> checkResetCode(String verificationCode,String email);

    List<UserResponse> searchUsers(String searchKey, int page, int limit);

    ResponseEntity<?> editProfile(String userUuid, MultipartFile profilePicture) throws IOException;

    UserResponse getProfile(String userUuid);

//    ResponseEntity<?> getPatients(Pageable pageable);
    PaginatedResponse<UserResponse> getInactiveUsers(String search, Pageable pageable);
}