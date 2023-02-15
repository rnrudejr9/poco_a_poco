package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.comment.CommentRequest;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.domain.dto.comment.ui.CommentViewResponse;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.service.CommentService;
import teamproject.pocoapoco.service.CommentViewService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentViewController {

    private final CommentService commentService;
    private final CommentViewService commentViewService;

    // 댓글 리스트 출력
    @GetMapping("/view/v1/crews/{crewId}/comments")
    public ResponseEntity getCommentList(@PathVariable Long crewId) {
        List<CommentViewResponse> list = commentViewService.getCommentViewList(crewId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    // 대댓글 리스트 출력
    @GetMapping("/view/v1/crews/{crewId}/comments/{parentId}/list")
    public ResponseEntity getChildCommentList(@PathVariable Long crewId, @PathVariable Long parentId) {
        List<CommentViewResponse> list = commentViewService.getChildCommentViewList(crewId,parentId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    // crew의 댓글 detail 출력
    @GetMapping("/view/v1/crews/{crewId}/comments/{commentId}")
    public ResponseEntity getDetailComment(@PathVariable Long crewId, @PathVariable Long commentId) {
        CommentResponse commentResponse = commentService.getDetailComment(crewId, commentId);
        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    // 댓글 작성
    @PostMapping("/view/v1/crews/{crewId}/comments")
    public ResponseEntity addComment(@RequestBody CommentRequest commentRequest,@PathVariable Long crewId, Authentication authentication) {
        CommentResponse commentResponse = commentService.addComment(commentRequest, crewId, authentication.getName());
        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    // 대댓글 작성
    @PostMapping("/view/v1/crews/{crewId}/comments/{parentId}")
    public ResponseEntity addCommentReply(@RequestBody CommentRequest commentRequest, @PathVariable String crewId, @PathVariable Long parentId, Authentication authentication) {
        commentViewService.addChildComment(commentRequest, parentId, authentication.getName());
        return new ResponseEntity<>("등록되었습니다.", HttpStatus.OK);
    }

    // 댓글, 대댓글 수정
    @PutMapping("/view/v1/crews/{crewId}/comments/{commentId}")
    public ResponseEntity modifyComment(@RequestBody CommentRequest commentRequest, @PathVariable Long crewId, @PathVariable Long commentId , Authentication authentication) {
        commentService.modifyComment(commentRequest, crewId, commentId, authentication.getName());
        return new ResponseEntity<>("수정되었습니다.", HttpStatus.OK);

    }
    // 댓글, 대댓글 삭제
    @DeleteMapping("/view/v1/crews/{crewId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long crewId, @PathVariable Long commentId , Authentication authentication) {
        try {
            if (commentService.getDetailComment(crewId, commentId).getUserName().equals(authentication.getName()) ||
                    authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                commentService.deleteComment(crewId, commentId, authentication.getName());
                return new ResponseEntity<>("articleCommentDeleting Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ErrorCode.INVALID_PERMISSION.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}