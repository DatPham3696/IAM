package com.example.security_demo.config;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.RoleUser;
import com.example.security_demo.entity.User;
import com.example.security_demo.enums.EnumRole;
import com.example.security_demo.repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public class CustomEvaluator implements PermissionEvaluator {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleUserRepository roleUserRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IRolePermissionRepository rolePermissionRepository;
    @Autowired
    private IPermissionRepository permissionRepository;

    public CustomEvaluator() {
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Long> roleIds = roleUserRepository.findAllByUserId(user.getId()).stream().map(RoleUser::getRoleId).toList();
        Long roleAdminId = roleRepository.findByCode(EnumRole.ADMIN.name()).get().getId();
        if (roleIds.contains(roleAdminId)) {
            return true;
        }
        Optional<Long> permissionId = permissionRepository.findPermissionIdByUserAndResourceCodeAndScope(user.getId(),
                targetDomainObject.toString(),
                permission.toString());
        if (permissionId.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

}
