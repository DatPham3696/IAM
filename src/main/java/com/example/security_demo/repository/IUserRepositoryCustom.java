package com.example.security_demo.repository;

import com.example.security_demo.dto.request.user.UserSearchRequest;
import com.example.security_demo.entity.User;

import java.util.List;

public interface IUserRepositoryCustom {
    List<User> searchUser(UserSearchRequest request);
    Long countUser(UserSearchRequest request);

}
