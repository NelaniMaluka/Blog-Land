package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String email;
    private String firstname;
    private String lastname;
    private Provider provider;
    private String profileIconUrl;
    private String location;
    private ExperienceLevel experience;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> socials;
}
