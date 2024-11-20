package com.example.security_demo.service;

import com.example.security_demo.dtos.roleDtos.RoleResponseDTO;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
}
