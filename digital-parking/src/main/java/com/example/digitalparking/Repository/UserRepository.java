package com.example.digitalparking.Repository;


import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    Optional<UserEntity> findByUserUuid(String userUuid);
    boolean existsByMobilePhone(String mobilePhone);
    UserEntity findByEmailAndPasswordResetCode(String email, String passwordResetCode);
    UserEntity findByEmailVerificationToken(String emailVerificationToken);
    Page<UserEntity> findByFirstNameContainingAndFatherNameContaining(String firstName, String fatherName,
                                                                      Pageable pageableRequest);
    Page<UserEntity> findByFirstNameContainingAndFatherNameContainingAndGrandFatherNameContaining(String firstName,
                                                                                                  String lastName, String grandFatherName, Pageable pageableRequest);
    Page<UserEntity> findByFirstNameContainingOrFatherNameContainingOrGrandFatherNameContainingOrMobilePhoneContainingOrEmailContainingOrUserStatusContaining(
            String searchKey, String searchKey2, String searchKey3, String searchKey4, String searchKey5, String searchKey6,
            Pageable pageableRequest);
    UserEntity findByEmailOrUserName(String email, String userName);
    List<UserEntity> findByRoleRoleName(String patient);
    Page<UserEntity> findAllByIsDeleted(boolean b, Pageable pageable);

    Page<UserEntity> findAllByIsDeletedAndFirstNameContainingOrFatherNameContainingOrMobilePhoneContaining(boolean b, String search, String search1, String search2, Pageable pageable);


    @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false AND u.role IS NOT NULL")
    Page<UserEntity> findAllActiveUsersWithRoles(Pageable pageable);

    // Pending users only
    Page<UserEntity> findAllByIsDeletedFalseAndUserStatus(Status status, Pageable pageable);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.isDeleted = false AND u.userStatus = :status " +
            "AND (u.firstName LIKE %:search% OR u.fatherName LIKE %:search% OR u.mobilePhone LIKE %:search%)")
    Page<UserEntity> searchUsersByStatus(@Param("status") Status status,
                                         @Param("search") String search,
                                         Pageable pageable);

}
