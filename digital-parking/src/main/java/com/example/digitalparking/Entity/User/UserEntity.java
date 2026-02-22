package com.example.digitalparking.Entity.User;

import com.example.digitalparking.Auditing.Audits.UserDateAudit;
import com.example.digitalparking.Entity.UserRole.Role;
import com.example.digitalparking.Enum.Gender;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",indexes = @Index(name = "idx_name_search", columnList = "firstName,fatherName"))
//@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 36, max = 40)
    private String userUuid = UUID.randomUUID().toString();
    @Size(min = 5, max = 50)
    @Email
    private String email;
    @Size(min = 5, max = 120)
    private String password;

    @Size(min = 2, max = 25)
    private String title;
    @NotBlank
    @Size(min = 2, max = 25)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String fatherName;

    private String grandFatherName;


    private Gender gender;

    @Size(min = 9, max = 13)
    @Column(nullable = false)
    private String mobilePhone;

    @Enumerated(EnumType.STRING)
    private Status userStatus;

    private String passwordResetCode;
    private String emailVerificationToken;
    private String profilePicture;
    private String userName;

    @Lob @Basic(fetch= FetchType.LAZY)
    @Column(name = "Profile")
    private byte[] imageData;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}

