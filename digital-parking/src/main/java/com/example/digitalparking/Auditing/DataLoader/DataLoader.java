package com.example.digitalparking.Auditing.DataLoader;
import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Entity.UserRole.Privilege;
import com.example.digitalparking.Entity.UserRole.Role;
import com.example.digitalparking.Enum.Gender;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Enum.UserType;
import com.example.digitalparking.Repository.PrivilegeRepository;
import com.example.digitalparking.Repository.Role.RoleRepository;
import com.example.digitalparking.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PrivilegeRepository privilegeRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            // === Privileges ===
            Privilege createUser = privilegeRepo.findByPrivilegeName("CREATE_USER")
                    .orElseGet(() -> {
                        Privilege p = new Privilege();
                        p.setPrivilegeName("CREATE_USER");
                        p.setPrivilegeDescription("Can create users");
                        p.setPrivilegeCategory("USER_MANAGEMENT");
                        return privilegeRepo.save(p);
                    });

            Privilege manageRoles = privilegeRepo.findByPrivilegeName("MANAGE_ROLE")
                    .orElseGet(() -> {
                        Privilege p = new Privilege();
                        p.setPrivilegeName("MANAGE_ROLE");
                        p.setPrivilegeDescription("Can manage roles and privileges");
                        p.setPrivilegeCategory("ROLE_MANAGEMENT");
                        return privilegeRepo.save(p);
                    });

// build the set outside
            Set<Privilege> superAdminPrivileges = Set.of(createUser, manageRoles);

            Role superAdmin = roleRepo.findByRoleName("SUPER_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("SUPER_ADMIN");
                        r.setRoleDescription("Super administrator with full access");
                        r.setPrivileges(superAdminPrivileges);
                        return roleRepo.save(r);
                    });





            // === Super Admin User ===
            UserEntity u = new UserEntity();
            u.setEmail("superadmin@edis.com");
            u.setPassword(passwordEncoder.encode("admin@123"));
            u.setTitle("Mr");
            u.setFirstName("Super");
            u.setFatherName("Admin");
            u.setGrandFatherName("System");
            u.setMobilePhone("0912345678");
            u.setGender(Gender.Male);
            u.setUserStatus(Status.ACTIVE);
            u.setRole(superAdmin);
            userRepo.save(u);
        }}}