package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UserResponse", description = "Represents a user profile including social links and account metadata")
public record UserResponse(
                @Schema(description = "User's email address", example = "[user@example.com](mailto:user@example.com)") String email,

                @Schema(description = "User's first name", example = "John") String firstname,

                @Schema(description = "User's last name", example = "Doe") String lastname,

                @Schema(description = "Authentication provider used by the user", example = "LOCAL") Provider provider,

                @Schema(description = "URL of the user's profile icon", example = "https://example.com/images/profile.jpg") String profileIconUrl,

                @Schema(description = "Location of the user", example = "Johannesburg, South Africa") String location,

                @Schema(description = "Experience level of the user", example = "NEW_BLOGGER") ExperienceLevel experience,

                @Schema(description = "User profile summary", example = "Software developer and blogger") String summary,

                @Schema(description = "User's professional title", example = "Java Developer") String title,

                @Schema(description = "Date when the user joined the platform", example = "2025-01-15T10:00:00") LocalDateTime joinedAt,

                @Schema(description = "Map of user's social platforms and their URLs", example = "{ \"twitter\": \"https://twitter.com/user\" }") @JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, String> socials

) {
}
