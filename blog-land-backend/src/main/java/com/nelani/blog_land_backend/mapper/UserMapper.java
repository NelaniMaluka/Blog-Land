package com.nelani.blog_land_backend.mapper;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.UserSocial;
import com.nelani.blog_land_backend.response.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse buildLoggedInUser(User user, List<UserSocial> socials) {
        Map<String, String> socialsMap = socials.stream()
                .collect(Collectors.toMap(UserSocial::getPlatform, UserSocial::getUrl));

        return UserResponse.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .provider(user.getProvider())
                .profileIconUrl(user.getProfileIconUrl())
                .location(user.getLocation())
                .experience(user.getExperience())
                .socials(socialsMap) // now a Map<String, String>
                .summary(user.getSummary())
                .title(user.getTitle())
                .joinedAt(user.getJoinedAt())
                .build();
    }

    public static UserResponse publicUserWithMinimalDetails(User user) {
        return UserResponse.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .profileIconUrl(user.getProfileIconUrl())
                .build();
    }

}
