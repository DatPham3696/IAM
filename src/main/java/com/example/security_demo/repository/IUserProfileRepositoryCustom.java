package com.example.security_demo.repository;

import com.example.security_demo.dto.request.userProfile.UserProfileSearchRequest;
import com.example.security_demo.entity.UserProfile;

import java.util.List;

public interface IUserProfileRepositoryCustom {
    List<UserProfile> searchUser(UserProfileSearchRequest request);

}
