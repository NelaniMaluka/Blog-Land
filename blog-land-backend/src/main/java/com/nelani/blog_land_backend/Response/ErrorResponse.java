package com.nelani.blog_land_backend.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "ErrorResponse", description = "Represents an error response with a code and message")
public record ErrorResponse(
                @Schema(description = "Error code or type", example = "Validation Error") String error,

                @Schema(description = "Detailed error message", example = "The provided email does not match the registered email for this account.") String message

) {
}
