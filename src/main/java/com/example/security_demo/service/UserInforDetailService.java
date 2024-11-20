package com.example.security_demo.service;

import com.example.security_demo.entity.User;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//public class UserInforDetailService implements UserDetailsService {
//    private final IUserRepository userRepository;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> users = userRepository.findByUserName(username);
//        return users.get();
//    }
//
//}
