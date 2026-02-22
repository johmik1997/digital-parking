package com.example.digitalparking.Entity.UserRole;


import com.example.digitalparking.Auditing.Audits.DateAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles",
        uniqueConstraints = { @UniqueConstraint(columnNames = "roleName"),
                @UniqueConstraint(columnNames = "roleUuid") ,}
        ,indexes = @Index(name = "idx_role_name", columnList = "roleName"))
public class Role extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String roleName;

    @NotBlank
    @Size(max = 100)
    private String roleDescription;

    @Size(min = 36, max = 40)
    private String roleUuid = UUID.randomUUID().toString();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_privileges",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Set<Privilege> privileges;
}

