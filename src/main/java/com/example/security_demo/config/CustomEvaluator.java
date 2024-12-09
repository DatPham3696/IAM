package com.example.security_demo.config;

import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RoleUser;
import com.example.security_demo.entity.User;
import com.example.security_demo.enums.EnumRole;
import com.example.security_demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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
        String searchKeyword;
        if (keycloakEnabled) {
            searchKeyword = authentication.getName();
        } else {
            searchKeyword = authentication.getName();
        }
        User user = getUserById(searchKeyword);

        if (hasAdminRole(user)) {
            return true;
        }
        return hasPermissionForResource(user, targetDomainObject, permission);
    }

    private User getUserById(String searchKeyword) {
        if (keycloakEnabled) {
            return userRepository.findByEmail(searchKeyword).orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            return userRepository.findByEmail(searchKeyword).orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

    private boolean hasAdminRole(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(EnumRole.ADMIN.name()));
        }
        return false;
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
