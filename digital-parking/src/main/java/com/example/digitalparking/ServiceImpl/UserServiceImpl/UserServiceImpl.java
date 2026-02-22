package com.example.digitalparking.ServiceImpl.UserServiceImpl;


import Utils.Image.ImageUtils;
import com.example.digitalparking.Dto.Request.Password.ChangePasswordRequest;
import com.example.digitalparking.Dto.Request.Password.ForgotPassword;
import com.example.digitalparking.Dto.Request.Password.ResetPasswordRequest;
import com.example.digitalparking.Dto.Request.User.AuthRequest;
import com.example.digitalparking.Dto.Request.User.CreateUserRequest;
import com.example.digitalparking.Dto.Request.User.SignUpRequest;
import com.example.digitalparking.Dto.Request.User.UserRequest;
import com.example.digitalparking.Dto.Response.MessageResponse;
import com.example.digitalparking.Dto.Response.PaginatedResponse.PaginatedResponse;
import com.example.digitalparking.Dto.Response.User.JwtResponse;
import com.example.digitalparking.Dto.Response.User.UserResponse;
import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Entity.UserRole.Role;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Repository.Role.RoleRepository;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.Service.UserService;
import com.example.digitalparking.exception.BadRequestException;
import com.example.digitalparking.exception.EmailAlreadyExists;
import com.example.digitalparking.exception.InvalidPhoneException;
import com.example.digitalparking.exception.ResourceNotFoundException;
import com.example.digitalparking.security.JwtUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository authRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${mail.custom.sender.name}")
    private String customSenderName;

    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository authRepository, RoleRepository roleRepository, JavaMailSender javaMailSender, TemplateEngine templateEngine, PasswordEncoder passwordEncoder,  PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }
    @Override
    public ResponseEntity<?> forgotPassword(ForgotPassword forgotPassword) {
        try {
            Optional<UserEntity> userByEmail = Optional.ofNullable(
                    authRepository.findByEmail(forgotPassword.getEmail())
            );

            UserEntity user = userByEmail.orElseThrow(() -> new RuntimeException("User not found."));

            String token = generateSixDigitNumber();

            // Save token to user
            user.setPasswordResetCode(token);
            authRepository.save(user);

            // Send email
            sendEmailToUser(forgotPassword.getEmail(), token);

        } catch (MessagingException e) {
            throw new BadRequestException("Email could not be sent. Please check your Email!");
        } catch (jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new MessageResponse("Password Reset Code Sent Successfully!"));
    }

    @Override
    public ResponseEntity<?> AuthenticateUser(AuthRequest authRequest) {

        UserEntity user = authRepository.findByEmail(authRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (user.getUserStatus() != Status.ACTIVE)
            throw new BadRequestException("your account is " + user.getUserStatus());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUserUuid(), userDetails.getEmail(), userDetails.getRoleUuid(), userDetails.getRoleName(),
                userDetails.getTitle(), userDetails.getFirstName(), userDetails.getFatherName(),
                userDetails.getGrandFatherName(), userDetails.getGender(), userDetails.getMobilePhone(),
                userDetails.getUserStatus(), userDetails.getUserType(),userDetails.getProfilePicture(),
                roles)); }

    @Override
    public ResponseEntity<?> createUser(CreateUserRequest createUserRequest){

        if (authRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExists("Email is Already in use");
        }
        if (authRepository.existsByMobilePhone(createUserRequest.getMobilePhone())) {
            throw new InvalidPhoneException("Phone is Already In use");
        }

        Role role = roleRepository.findByRoleUuid(createUserRequest.getRoleUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Role not Found!"));
        UserEntity user = new UserEntity();

        BeanUtils.copyProperties(createUserRequest, user);
        user.setUserStatus(Status.ACTIVE);
        user.setRole(role);
        user.setPassword(encoder.encode(createUserRequest.getPassword()));

        UserEntity savedUser = authRepository.save(user);
        return ResponseEntity.ok("user created Successfully");
    }

    @Override
    public ResponseEntity<?> Save(SignUpRequest signUpRequest) {
        if (authRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExists("Email is Already in use");
        }

        if (authRepository.existsByMobilePhone(signUpRequest.getMobilePhone())) {
            throw new InvalidPhoneException("Phone is Already In use");
        }

        Role role = roleRepository.findByRoleUuid(signUpRequest.getRoleUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Role not Found!"));

        UserEntity user = new UserEntity();
        user.setUserStatus(Status.ACTIVE);
        BeanUtils.copyProperties(signUpRequest, user);
        user.setRole(role);

        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        UserEntity savedUser = authRepository.save(user);

        return ResponseEntity.ok("User Registered Successfully. Verify Your Email Please");
    }


    @Override
    public ResponseEntity<MessageResponse> updateUser(String userUuid, UserRequest userRequest) {

        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found!"));

        var role = roleRepository.findByRoleUuid(userRequest.getRoleUuid())
                .orElse(null);

        BeanUtils.copyProperties(userRequest, user);
        user.setRole(role);

//        user.setPharmacyUuid(userRequest.getBranchId());
        authRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }


    @Override
    public ResponseEntity<?> deleteUser(String userUuid) {

        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        user.setDeleted(true);
        authRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User soft deleted successfully!"));
    }

    @Override
    public UserResponse getUser(String userUuid) {

        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        userResponse.setRoleName(user.getRole().getRoleName());
        userResponse.setRoleUuid(user.getRole().getRoleUuid());
        return userResponse;
    }

    @Override
    public ResponseEntity<?> sendPasswordResetCode(String email) {
        try {
            Optional<UserEntity> userByEmail = Optional.ofNullable(authRepository.findByEmail(email));
            if (userByEmail.isEmpty())
                throw new RuntimeException("User not found.");

            String token = generateSixDigitNumber();

//            createPasswordResetTokenForUser(userByEmail.get(), token);
            userByEmail.get().setPasswordResetCode(token);
            authRepository.save(userByEmail.get());
            sendEmailToUser(email,token);

        }catch (MessagingException e){
            throw new BadRequestException("Email is Already sent Please check your Email!");
        } catch (jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new MessageResponse("Password Reset Code Sent Successfully!"));
    }

    private void sendEmailToUser(String email, String token) throws jakarta.mail.MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("resetCode", token);
        context.setVariable("header","Password Reset Request");

        UserEntity forEmail = authRepository.findByEmail(email);
        if(forEmail==null) throw new BadRequestException("No User Found With The Provided Email");

        context.setVariable("userName",forEmail.getFirstName());

        String content = templateEngine.process("reset-password", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Set true for multipart message
        helper.setSubject(" Your Password Reset Request: "+ email);
        helper.setFrom(username,customSenderName);
        helper.setTo(email);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    private String generateSixDigitNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);

        return String.format("%06d", number);
    }

    @Override
    public PaginatedResponse<UserResponse> getUsers(String search, Pageable pageable) {
        return search != null ? getUsersWithSearch(search, pageable) :
                getAllUsers(pageable);
    }

    private PaginatedResponse<UserResponse> getUsersWithSearch(String search, Pageable pageable) {

        Page<UserEntity> usersPage = authRepository.findAllByIsDeletedAndFirstNameContainingOrFatherNameContainingOrMobilePhoneContaining(false,search,search,search,pageable);

        List<UserResponse> userList = usersPage.stream()
                .filter(userEntity -> !(userEntity.getRole() == null))
                .map(this::mapToUserResponse)
                .toList();

        return new PaginatedResponse<>(
                userList,
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.getSize()
        );
    }

    private PaginatedResponse<UserResponse> getAllUsers(Pageable pageable) {

        Page<UserEntity> usersPage = authRepository.findAllActiveUsersWithRoles(pageable);
        System.out.println("total users"+usersPage.getSize());
        List<UserResponse> userList = usersPage.stream()
                .map(this::mapToUserResponse)
                .toList();

        return PaginatedResponse.fromPage(new PageImpl<>(userList,usersPage.getPageable(),userList.size()));

    }



    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail) {

        UserEntity userEntity = authRepository.findByEmail(resetPasswordDetail.getEmail());
        if(userEntity == null) 	throw new RuntimeException("email is not correct.");
        if (!resetPasswordDetail.getNewPassword().equals(resetPasswordDetail.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match.");
        userEntity.setPassword(encoder.encode(resetPasswordDetail.getNewPassword()));
        authRepository.save(userEntity);
        String  returnValue = "Password changed successfully";

        return ResponseEntity.ok(new MessageResponse(returnValue));
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest resetPasswordDetail, String userUuid) {


        var user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if(passwordEncoder.matches(resetPasswordDetail.getOldPassword(), user.getPassword())){
            if (resetPasswordDetail.getNewPassword().equals(resetPasswordDetail.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(resetPasswordDetail.getNewPassword()));

                authRepository.save(user);
            } else {
                throw new BadRequestException("password Doesn't match with confirm password");
            }
        }else {
            throw new BadRequestException("Please Enter your valid password to change it!");
        }

        return ResponseEntity.ok("Password Changed Successfully");
    }

    @Override
    public ResponseEntity<?> verifyAccount(String emailVerificationToken) {
        String returnValue = "";
        UserEntity user = authRepository.findByEmailVerificationToken(emailVerificationToken);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setUserStatus(Status.ACTIVE);
        UserEntity updatedUser = authRepository.save(user);
        if (updatedUser.getUserStatus() == Status.ACTIVE) {
            returnValue = "Account Verified Successfully";
        }
        return ResponseEntity.ok(new MessageResponse(returnValue));
    }


    @Override
    public ResponseEntity <?> checkResetCode(String verificationCode, String email) {
        UserEntity userEntity = authRepository.findByEmailAndPasswordResetCode(email,verificationCode);
        if(userEntity == null) throw new RuntimeException("Invalid Reset Code.");
        return ResponseEntity.ok(new MessageResponse("Reset Code is valid"));

    }

    @Override
    public List<UserResponse> searchUsers(String searchKey, int page, int limit) {

        if(page > 0) page = page - 1;
        String[] searchKeys = searchKey.split(" ");

        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        int countSpaces = StringUtils.countOccurrencesOf(searchKey, " ");

        Page<UserEntity> usersPage = null;

        if(countSpaces == 0) {
            usersPage = authRepository.findByFirstNameContainingOrFatherNameContainingOrGrandFatherNameContainingOrMobilePhoneContainingOrEmailContainingOrUserStatusContaining(searchKey,searchKey,searchKey,searchKey,searchKey, searchKey, pageableRequest);
        }

        else if(countSpaces == 1){
            String firstName = searchKeys[0];
            String fatherName = searchKeys[1];
            usersPage = authRepository.findByFirstNameContainingAndFatherNameContaining(firstName,fatherName,pageableRequest);
        }

        else if(countSpaces == 2) {
            String firstName = searchKeys[0];
            String fatherName = searchKeys[1];
            String grandFatherName = searchKeys[2];
            usersPage = authRepository.findByFirstNameContainingAndFatherNameContainingAndGrandFatherNameContaining(firstName,fatherName,grandFatherName,pageableRequest);
        }


        assert usersPage != null;
        int totalPages = usersPage.getTotalPages();
        List<UserEntity> users = usersPage.getContent();
        List<UserResponse> userResponse = new ArrayList<>();

        for (UserEntity u : users) {
            UserResponse ur = new UserResponse();

            if(userResponse.isEmpty())
                ur.setTotalPages(totalPages);
            BeanUtils.copyProperties(u, ur);
            userResponse.add(ur);
        }

        return userResponse;
    }

    @Override
    public ResponseEntity<?> editProfile(String userUuid, MultipartFile profilePicture) {

        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));


//        if (profilePicture==null){
//
//            user.setFirstName(getUser().getFirstName());
//        }else {
//
//        byte[] profilePictureBytes = profilePicture.getBytes();
//
//        if (profilePictureBytes.length > 10485760)
//            throw new BadRequestException("Profile should be less than 1mb");
//
//        user.setImageData(ImageUtils.compressImage(profilePictureBytes));


        authRepository.save(user);

        return ResponseEntity.ok("profile changed successfully");

    }

    @Override
    public UserResponse getProfile(String userUuid) {
        UserEntity profile = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        UserResponse response = new UserResponse();
        if (profile.getUserName()==null||profile.getUserName().isEmpty())
            response.setUsername(profile.getFirstName());
        else response.setUsername(profile.getUserName());
        BeanUtils.copyProperties(profile, response);
        return response;
    }

//    @Override
//    public ResponseEntity<?> getPatients(Pageable pageable) {
//
//        List <UserEntity> patients=authRepository.findByRoleRoleName("patient");
//        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
//        Instant oneMonthAgoInstant = LocalDate.now()
//                .minusMonths(1)                 // Subtract one month
//                .atStartOfDay()                  // Get the start of the day (midnight)
//                .atZone(ZoneId.systemDefault())  // Convert to the system's default time zone
//                .toInstant();
//        List<UserEntity> p= patients.stream().filter(userEntity -> userEntity.getCreatedAt()!=null&&userEntity.getCreatedAt().isBefore(oneMonthAgoInstant)).toList();
//        int totalPatients=patients.size();
//        int returningPatients= p.size();
//        int newPatients;
//        Map<String, Integer> responseMap = new HashMap<>();
//        responseMap.put("totalPatients", totalPatients);
//        responseMap.put("returningPatients", returningPatients);
//        responseMap.put("newPatients",totalPatients-returningPatients);
//        return ResponseEntity.ok(responseMap);
//    }

    private UserResponse mapToUserResponse(UserEntity user){

        var response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setRoleName(user.getRole().getRoleName());
        response.setRoleUuid(user.getRole().getRoleUuid());
        response.setGender(user.getGender());
        response.setCreatedAt(user.getCreatedAt());


        return response;
    }


    @Override
    public ResponseEntity<?> updateUserStatus(String userUuid){
        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        user.setUserStatus(Status.ACTIVE);
        authRepository.save(user);
        return ResponseEntity.ok("The user is active now");
    }

    public PaginatedResponse<UserResponse> getInactiveUsers(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return getInactiveUsersWithSearch(search,pageable);
        }
        return getPendingUsers(pageable);
    }

    private PaginatedResponse<UserResponse> getPendingUsers(Pageable pageable) {
        Page<UserEntity> usersPage = authRepository.findAllByIsDeletedFalseAndUserStatus(Status.PENDING, pageable);

        List<UserResponse> userList = usersPage.stream()
                .filter(user -> user.getRole() != null) // exclude those without role
                .map(this::mapToUserResponse)
                .toList();

        return new PaginatedResponse<>(
                userList,
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.getSize()
        );
    }

    private PaginatedResponse<UserResponse> getInactiveUsersWithSearch(String search, Pageable pageable) {
        Page<UserEntity> usersPage = authRepository.searchUsersByStatus(Status.PENDING, search, pageable);

        List<UserResponse> userList = usersPage.stream()
                .filter(user -> user.getRole() != null)
                .map(this::mapToUserResponse)
                .toList();

        return new PaginatedResponse<>(
                userList,
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.getSize()
        );
    }


}
