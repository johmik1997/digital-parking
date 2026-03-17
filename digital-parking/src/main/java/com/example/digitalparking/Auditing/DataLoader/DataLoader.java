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

       

// build the set outside
            Set<Privilege> superAdminPrivileges = Set.of(createUser);

            Role superAdmin = roleRepo.findByRoleName("Super Admin")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("Super Admin");
                        r.setRoleDescription("Super administrator with full access");
                        r.setPrivileges(superAdminPrivileges);
                        return roleRepo.save(r);
                    });
             Role cashier = roleRepo.findByRoleName("Cashier")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("Cashier");
                        r.setRoleDescription("Cashier with limited access");
                        r.setPrivileges(Set.of());

                        return roleRepo.save(r);
                    });
             Role customer = roleRepo.findByRoleName("Customer")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("Customer");
                        r.setRoleDescription("Regular customer");
                        r.setPrivileges(Set.of());

                        return roleRepo.save(r);
                    });





            // === Super Admin User ===
            UserEntity u = new UserEntity();
            u.setEmail("superadmin@gmail.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setTitle("Mr");
            u.setFirstName("Super");
            u.setFatherName("Admin");
            u.setGrandFatherName("System");
            u.setMobilePhone("0912345678");
            u.setGender(Gender.Male);
            u.setUserStatus(Status.ACTIVE);
            u.setRole(superAdmin);
            userRepo.save(u);

         // === Cashier  User ===
             UserEntity u = new UserEntity();
            u.setEmail("cashier@gmail.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setTitle("Mr");
            u.setFirstName("Cashier");
            u.setFatherName("Admin");
            u.setGrandFatherName("System");
            u.setMobilePhone("0911345678");
            u.setGender(Gender.Male);
            u.setUserStatus(Status.ACTIVE);
            u.setRole(cashier);
            userRepo.save(u);

              // === Customer  User ===
             UserEntity u = new UserEntity();
            u.setEmail("customer@gmail.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setTitle("Mr");
            u.setFirstName("Customer");
            u.setFatherName("Admin");
            u.setGrandFatherName("System");
            u.setMobilePhone("0911245678");
            u.setGender(Gender.Male);
            u.setUserStatus(Status.ACTIVE);
            u.setRole(customer);
            userRepo.save(u);
        }}}