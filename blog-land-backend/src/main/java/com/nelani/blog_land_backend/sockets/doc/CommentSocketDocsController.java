package com.nelani.blog_land_backend.sockets.doc;

import com.nelani.blog_land_backend.response.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "WebSocket Comment Updates", description = "STOMP WebSocket channels for sending real-time comment events and updates.")
public class CommentSocketDocsController {

        @Operation(summary = "Broadcast updated comment count for post", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/comments/count/{postId}
                        ```
                        Fired whenever a post’s total comment count changes.

                        **Subscribers receive:**
                        - A number (`long`) representing the updated comment count.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Updated comment count", content = @Content(schema = @Schema(implementation = Long.class)))
        })
        @GetMapping("/docs/ws/comments/count")
        public void commentCountDoc() {
        }

        @Operation(summary = "Broadcast newly added comment", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/comments/add/{postId}
                        ```
                        Fired when a new comment is added to a post.

                        **Subscribers receive:**
                        - A `CommentResponse` object containing the new comment.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "CommentResponse payload", content = @Content(schema = @Schema(implementation = CommentResponse.class)))
        })
        @GetMapping("/docs/ws/comments/add")
        public void commentAddDoc() {
        }

        @Operation(summary = "Broadcast updated comment", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/comments/update/{postId}
                        ```
                        Fired when an existing comment is edited.

                        **Subscribers receive:**
                        - A `CommentResponse` object containing the updated comment.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "Updated CommentResponse", content = @Content(schema = @Schema(implementation = CommentResponse.class)))
        })
        @GetMapping("/docs/ws/comments/update")
        public void commentUpdateDoc() {
        }

        @Operation(summary = "Broadcast deleted comment ID", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /topic/posts/comments/remove/{postId}
                        ```
                        Fired when a comment is deleted.

                        **Subscribers receive:**
                        - The UUID of the deleted comment.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "UUID of deleted comment", content = @Content(schema = @Schema(implementation = String.class)))
        })
        @GetMapping("/docs/ws/comments/remove")
        public void commentRemoveDoc() {
        }

        @Operation(summary = "Private event: user added a comment", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /user/queue/posts/comment/add/{postId}
                        ```
                        Sends a **private** message only to the comment owner.

                        **Subscribers receive:**
                        - The UUID of the newly added comment.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "UUID of created comment", content = @Content(schema = @Schema(implementation = String.class)))
        })
        @GetMapping("/docs/ws/comments/user/add")
        public void userCommentAddDoc() {
        }

        @Operation(summary = "Private event: user removed their comment", description = """
                        **WebSocket/STOMP Destination:**
                        ```
                        /user/queue/posts/comment/remove/{postId}
                        ```
                        Sends a **private** message only to the comment owner.

                        **Subscribers receive:**
                        - The UUID of the removed comment.
                        """, responses = {
                        @ApiResponse(responseCode = "200", description = "UUID of removed comment", content = @Content(schema = @Schema(implementation = String.class)))
        })
        @GetMapping("/docs/ws/comments/user/remove")
        public void userCommentRemoveDoc() {
        }

}
