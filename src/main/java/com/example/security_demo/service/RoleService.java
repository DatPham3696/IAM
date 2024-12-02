package com.example.security_demo.service;

import com.example.security_demo.entity.Role;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    public Role addRole(Role role){
        if(roleRepository.findByCode(role.getCode()).isPresent()){
            throw new IllegalArgumentException("code existed");
        }
        return roleRepository.save(role);
    }
    public void deleteRoleById(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Cant find role" + id);
        }
        roleRepository.deleteById(id);
    }
}
