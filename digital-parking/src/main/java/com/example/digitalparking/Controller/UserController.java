package com.example.digitalparking.Controller;

import Utils.Pagination.PaginationUtils;
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
import com.example.digitalparking.Service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/auth/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserController( AuthenticationManager authenticationManager,UserService userService) {
        this.userService = userService;
        this.authenticationManager=authenticationManager;
    }
    @PostMapping("/signUp")
    public ResponseEntity<?> saveUser(@Valid @RequestBody SignUpRequest signUpRequest){
        return userService.Save(signUpRequest);
    }


    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody AuthRequest authRequest){
        return userService.AuthenticateUser(authRequest);
    }
    @PostMapping("/create/user")
    @PreAuthorize("hasRole('ROLE_create_user')")
    public ResponseEntity<?> createUSer(@Valid @RequestBody CreateUserRequest createUserRequest){
        return userService.createUser(createUserRequest);
    }
    @PutMapping(path = "/update/{userUuid}")
    @PreAuthorize("hasRole('ROLE_edit_profile')")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable String userUuid, @RequestBody UserRequest userRequest) {
        return userService.updateUser(userUuid, userRequest);
    }
    @PutMapping(path = "/update/status/{userUuid}")
//    @PreAuthorize("hasRole('ROLE_update_status")
    public ResponseEntity<?>updateStatus(@PathVariable String userUuid){
        return userService.updateUserStatus(userUuid);
    }

    @GetMapping(path = "/{userUuid}")
//    @PreAuthorize("hasRole('ROLE_read_user')")
    public UserResponse getUser(@PathVariable String userUuid) {
        return userService.getUser(userUuid);
    }


    @DeleteMapping(path = "/delete/{userUuid}")
    @PreAuthorize("hasRole('Delete-User')")
    public ResponseEntity<?> deleteUser(@PathVariable String userUuid) {
        return userService.deleteUser(userUuid);


    }
//
//    @GetMapping("/getPatients")
//    @PreAuthorize("hasRole('Read-Payer-Users')")
//    public ResponseEntity<?> getPatients(@RequestParam(value="page", defaultValue = "1") int page,
//                                         @RequestParam(value="limit", defaultValue = "25") int limit) {
//        Pageable pageable = PaginationUtils.paginateResource(page,limit,"id","desc");
//        return userService.getPatients(pageable);
//    }

    @GetMapping(path = "/all")
//    @PreAuthorize("hasRole('ROLE_read_user')")
    public PaginatedResponse<UserResponse> getUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value="limit", defaultValue = "25") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Pageable pageable = PaginationUtils.paginateResource(page,limit,sortBy,sortDirection);
        return userService.getUsers(search,pageable);
    }

    @GetMapping(path = "/search")
    @PreAuthorize("hasRole('ROLE_read_user')")
    public List<UserResponse> searchUsers(@RequestParam("search") String searchKey, @RequestParam(value="page", defaultValue = "1") int page,
                                          @RequestParam(value="limit", defaultValue = "25") int limit){
        return userService.searchUsers(searchKey,page,limit);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordDetail){
        return userService.resetPassword(resetPasswordDetail);
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassoword(@RequestBody ForgotPassword forgotPassword){
        return userService.forgotPassword(forgotPassword);
    }
    @PutMapping(path = "/changePassword/{userUuid}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                            @PathVariable String userUuid) {
        return userService.changePassword(changePasswordRequest, userUuid);
    }

    @GetMapping(path = "/email/verification/{emailVerificationToken}")
    @PreAuthorize("hasRole('Email-Verification')")
    public ResponseEntity<?> verifyAccount(@PathVariable String emailVerificationToken) {
        return userService.verifyAccount(emailVerificationToken);

    }


    @PutMapping(path = "/password/sendResetCode/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable String email)
            throws  MessagingException {
        return userService.sendPasswordResetCode(email);

    }

    @PostMapping(path = "/password/checkResetCode")
    public ResponseEntity<?> checkResetCode(@RequestParam String verificationCode,
                                            @RequestParam String email) {
        return userService.checkResetCode(verificationCode, email);
    }

    @PutMapping(value = "/editProfile/{userUuid}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>editProfile(@PathVariable String userUuid,
                                        @RequestPart(required = false) MultipartFile profilePicture
    ) throws IOException {
        log.info("in the controller");
        return userService.editProfile(userUuid,profilePicture);
    }

    @GetMapping("/getProfile/{userUuid}")
//    @PreAuthorize("hasRole('ROLE_read_user')")
    public UserResponse getProfile(@PathVariable String userUuid)
    {return userService.getProfile(userUuid);
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/jpg")&&
                !contentType.equals("image/gif"));
    }


    @GetMapping("/get/inactive/users")
    @PreAuthorize("hasRole('ROLE_create_drugs')")
    public PaginatedResponse<UserResponse> getInactiveUsers(@RequestParam(value = "search", required = false) String search,
                                                            @RequestParam(value="page", defaultValue = "1") int page,
                                                            @RequestParam(value="limit", defaultValue = "25") int limit,
                                                            @RequestParam(defaultValue = "id") String sortBy,
                                                            @RequestParam(defaultValue = "desc") String sortDirection){

        Pageable pageable = PaginationUtils.paginateResource(page,limit,sortBy,sortDirection);
        return userService.getInactiveUsers(search,pageable);

    }

}