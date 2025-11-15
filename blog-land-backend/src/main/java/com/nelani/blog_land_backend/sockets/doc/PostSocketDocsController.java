package com.nelani.blog_land_backend.sockets.doc;

import com.nelani.blog_land_backend.response.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "WebSocket Post Updates", description = "STOMP WebSocket channels for real-time post creation, update, and deletion events.")
public class PostSocketDocsController {

        @Operation(summary = "Broadcast new post", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/add/{postId}
                        ```
                        Fired when a new post is created.

                        **Subscribers will receive:**
                        - A `PostResponse` object containing the post data
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example structure of the PostResponse WebSocket payload", content = @Content(schema = @Schema(implementation = PostResponse.class)))
        })
        @GetMapping("/docs/ws/posts/add")
        public void postAddDoc() {
        }

        @Operation(summary = "Broadcast updated post", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/update/{postId}
                        ```
                        Fired when an existing post is updated.

                        **Subscribers will receive:**
                        - A `PostResponse` object with the updated post details
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example structure of the PostResponse WebSocket payload", content = @Content(schema = @Schema(implementation = PostResponse.class)))
        })
        @GetMapping("/docs/ws/posts/update")
        public void postUpdateDoc() {
        }

        @Operation(summary = "Broadcast deleted post event", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/remove/{postId}
                        ```
                        Fired when a post is deleted.

                        **Subscribers will receive:**
                        - The deleted post's UUID as the message body
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Example deleted post UUID", content = @Content(schema = @Schema(implementation = String.class)))
        })
        @GetMapping("/docs/ws/posts/remove")
        public void postRemoveDoc() {
        }

}
