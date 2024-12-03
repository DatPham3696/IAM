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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Value("${idp.enabled}")
    private boolean keycloakEnabled;
    public CustomEvaluator() {
    }
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String userId;
        if (keycloakEnabled) {
            userId = authentication.getName();
        } else {
            userId = authentication.getName();
        }
        User user = getUserById(userId);

        if (hasAdminRole(user)) {
            return true;
        }
        return hasPermissionForResource(user, targetDomainObject, permission);
    }

    private User getUserById(String userId) {
        if (keycloakEnabled) {
            return userRepository.findByKeyclUserId(userId);
        } else {
            return userRepository.findByEmail(userId).orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

    private boolean hasAdminRole(User user) {
        List<Long> roleIds = roleUserRepository.findAllByUserId(user.getId()).stream()
                .map(RoleUser::getRoleId)
                .collect(Collectors.toList());

        Long roleAdminId = roleRepository.findByCode(EnumRole.ADMIN.name())
                .map(role -> role.getId())
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        return roleIds.contains(roleAdminId);
    }

    private boolean hasPermissionForResource(User user, Object targetDomainObject, Object permission) {
        return permissionRepository.findPermissionIdByUserAndResourceCodeAndScope(user.getId(),
                        targetDomainObject.toString(),
                        permission.toString())
                .isPresent();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

}
