package com.nelani.blog_land_backend.sockets.doc;

import com.nelani.blog_land_backend.response.LikeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "WebSocket Likes Updates", description = "STOMP WebSocket channels for sending real-time like count updates and user like lists.")
public class LikesSocketDocsController {

        @Operation(summary = "Broadcast updated post like count", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/likes/{postId}
                        ```
                        Fired whenever a post’s total like count changes.

                        **Subscribers receive:**
                        - A number (`long`) representing the updated like count.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example updated like count", content = @Content(schema = @Schema(implementation = Long.class)))
        })
        @GetMapping("/docs/ws/posts/likes")
        public void postLikesDoc() {
        }

        @Operation(summary = "Send updated user's liked posts", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /user/queue/user/posts/likes/update
                        ```
                        Sends a *private list* of liked posts to the specific user.

                        **Subscribers receive:**
                        - A list of `LikeResponse` containing post details the user liked.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "List of LikeResponse objects", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LikeResponse.class))))
        })
        @GetMapping("/docs/ws/user/likes")
        public void userLikesDoc() {
        }

}
