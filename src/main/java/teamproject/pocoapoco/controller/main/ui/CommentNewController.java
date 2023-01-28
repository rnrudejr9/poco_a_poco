package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.comment.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CommentService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentNewController {

    private final CommentService commentService;

    @PostMapping("/view/v1/crews/{crewId}/comments")
    public Response<CommentResponse> addComment(@RequestBody CommentRequest commentRequest, @PathVariable Long crewId, Authentication authentication) {
        return Response.success(commentService.addComment(commentRequest, crewId, authentication.getName()));
    }

    @PostMapping("/view/v1/crews/{crewId}/comments/{parentCommentId}")
    public Response<CommentReplyResponse> addCommentReply(@RequestBody CommentReplyRequest commentReplyRequest, @PathVariable Long crewId, @PathVariable Long parentCommentId, Authentication authentication) {
        return Response.success(commentService.addCommentReply(commentReplyRequest, crewId, parentCommentId, authentication.getName()));
    }

    @GetMapping("/view/v1/crews/{crewId}/comments")
    public Response<Page<CommentResponse>> getCommentList(@PathVariable Long crewId, Pageable pageable) {
        return Response.success(commentService.getCommentList(pageable, crewId));
    }

    @GetMapping("/view/v1/crews/{crewId}/comments/{parentCommentId}/list")
    public Response<Page<CommentReplyResponse>> getCommentReplyList(@PathVariable Long crewId, Long parentCommentId, Pageable pageable) {
        return Response.success(commentService.getCommentReplyList(pageable, crewId, parentCommentId));
    }

    @PutMapping("/view/v1/crews/{crewId}/comments/{commentId}")
    public Response<CommentResponse> modifyComment(@RequestBody CommentRequest commentRequest, @PathVariable Long crewId, @PathVariable Long commentId , Authentication authentication) {
        return Response.success(commentService.modifyComment(commentRequest, crewId, commentId, authentication.getName()));
    }

    @DeleteMapping("/view/v1/crews/{crewId}/comments/{commentId}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Long crewId, @PathVariable Long commentId , Authentication authentication) {
        return Response.success(commentService.deleteComment(crewId, commentId , authentication.getName()));
    }

    @GetMapping("/crew/{crewId}/comments/{commentId}")
    public Response<CommentResponse> getDetailComment(@PathVariable Long crewId, @PathVariable Long commentId, Authentication authentication) {
        return Response.success(commentService.getDetailComment(crewId, commentId));
    }
}

