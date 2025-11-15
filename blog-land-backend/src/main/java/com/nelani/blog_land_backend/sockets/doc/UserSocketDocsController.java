package com.nelani.blog_land_backend.sockets.doc;

import com.nelani.blog_land_backend.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "WebSocket User Updates", description = "Documentation for STOMP WebSocket channels used to deliver user updates.")
public class UserSocketDocsController {

        @Operation(summary = "Private user update WebSocket channel", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /user/queue/user/update
                        ```
                        Sends *private* updates to a specific user whenever their account or social links change.

                        - Delivery is user-specific
                        - Requires an authenticated WebSocket session
                        - Message payload = `UserResponse`
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example structure of the WebSocket message payload", content = @Content(schema = @Schema(implementation = UserResponse.class)))
        })
        @GetMapping("/docs/ws/private-user-update")
        public void privateUserUpdateDoc() {
        }

        @Operation(summary = "Public user update broadcast channel", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/user/update/{naniId}
                        ```
                        Broadcasts *public profile updates* of a user.

                        - Anyone subscribed to `/topic/user/update/{naniId}` receives updates
                        - Does not require authentication
                        - Message payload = `UserResponse`
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example structure of the WebSocket message payload", content = @Content(schema = @Schema(implementation = UserResponse.class)))
        })
        @GetMapping("/docs/ws/public-user-update")
        public void publicUserUpdateDoc() {
        }
}
