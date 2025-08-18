package com.nelani.blog_land_backend.Util.Builders;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.UserResponse;

public class UserBuilder {

    public static UserResponse buildLoggedInUser(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setProvider(user.getProvider());
        userResponse.setProfileIconUrl(user.getProfileIconUrl());
        userResponse.setLocation(user.getLocation());
        userResponse.setExperience(user.getExperience());
        userResponse.setSocials(user.getSocials());
        userResponse.setSummary(user.getSummary());
        userResponse.setTitle(user.getTitle());
        userResponse.setJoinedAt(user.getJoinedAt());
        return userResponse;
    }

    public static UserResponse publicUser(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setProfileIconUrl(user.getProfileIconUrl());
        userResponse.setLocation(user.getLocation());
        userResponse.setExperience(user.getExperience());
        userResponse.setSocials(user.getSocials());
        userResponse.setSummary(user.getSummary());
        userResponse.setTitle(user.getTitle());
        userResponse.setJoinedAt(user.getJoinedAt());
        return userResponse;
    }

    public static UserResponse publicUserWithMinimalDetails(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setProfileIconUrl(user.getProfileIconUrl());
        return userResponse;
    }
}
