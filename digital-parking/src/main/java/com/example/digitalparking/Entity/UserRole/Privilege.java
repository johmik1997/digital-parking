package com.example.digitalparking.Entity.UserRole;

import com.example.digitalparking.Auditing.Audits.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges", uniqueConstraints = { @UniqueConstraint(columnNames = "privilegeName"),
        @UniqueConstraint(columnNames = "privilegeUuid")

})
public class Privilege extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 50,nullable = false)
    private String privilegeName;

    @Size(min = 36, max = 40)
    private String privilegeUuid = UUID.randomUUID().toString();

    @Size(max = 100)
    private String privilegeDescription;

    @Column(length = 50)
    private String privilegeCategory;


    @JsonBackReference
    @ManyToMany(mappedBy = "privileges")
    private List<Role> roles= new ArrayList<>();

    public Privilege(String createPrivilege, String s, String privilege) {

        super();
    }
}

