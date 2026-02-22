package com.example.digitalparking.ServiceImpl.UserServiceImpl;

import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.exception.EmailAlreadyExists;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        UserEntity user = userRepository.findByEmailOrUserName(email,email);
        if (user==null) throw new EmailAlreadyExists("User Not Found with email: " + email);

        List<String> privilegesForRole = user.getRole().getPrivileges().stream().map(privilege -> "ROLE_" + privilege.getPrivilegeName()).toList();



        return UserDetailsImpl.build(user,privilegesForRole);
    }






}