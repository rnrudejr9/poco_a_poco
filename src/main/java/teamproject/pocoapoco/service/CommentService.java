package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamproject.pocoapoco.domain.dto.comment.*;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.AlarmType;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.AlarmRepository;
import teamproject.pocoapoco.repository.CommentRepository;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.time.LocalDateTime;

import static teamproject.pocoapoco.controller.main.api.sse.SseController.sseEmitters;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final AlarmRepository alarmRepository;

    public Page<CommentResponse> getCommentList(Pageable pageable, Long crewId) {
        Page<Comment> list = commentRepository.findByCrewId(crewId, pageable);
        return list.map(CommentResponse::of);
    }
    public Page<CommentReplyResponse> getCommentReplyList(Pageable pageable, Long crewId, Long parentCommentId) {
        Page<Comment> list = commentRepository.findByCrewIdAndParentId(crewId, pageable, parentCommentId);
        return list.map(CommentReplyResponse::of);
    }
    public CommentResponse addComment(CommentRequest commentRequest, Long crewId, String userName) {
        User user = getUser(userName);
        Crew crew = getCrew(crewId);

        Comment comment = commentRepository.save(commentRequest.toEntity(user, crew));
        alarmRepository.save(Alarm.toEntity(user, crew, AlarmType.ADD_COMMENT, comment.getComment()));

        //sse 로직
        if (sseEmitters.containsKey(crew.getUser().getUsername())) {
            log.info("userName이 Map으로 등록되어있어 알림 sse 작동됩니다.");
            log.info("Sse username = {}", crew.getUser().getUsername());
            SseEmitter sseEmitter = sseEmitters.get(crew.getUser().getUsername());
            try {
                sseEmitter.send(SseEmitter.event().name("alarm").data(
                        user.getNickName() + "님이 \"" + crew.getTitle() + "\"모임에 댓글을 남겼습니다."));
            } catch (Exception e) {
                sseEmitters.remove(crew.getUser().getUsername());
            }
        }

        return CommentResponse.of(comment);
    }
    public CommentReplyResponse addCommentReply(CommentReplyRequest commentReplyRequest, Long crewId, Long parentCommentId, String userName) {
        User user = getUser(userName);
        Crew crew = getCrew(crewId);
        Comment parentComment = getComment(parentCommentId);

        Comment comment = commentRepository.save(commentReplyRequest.toEntity(user, crew, parentComment));
        alarmRepository.save(Alarm.toEntity(user, crew, AlarmType.ADD_COMMENT, comment.getComment()));

        return CommentReplyResponse.of(comment);
    }


    public CommentResponse modifyComment(CommentRequest commentRequest, Long crewId, Long commentId, String userName) {
        Comment comment = checkCommentAndCrew(crewId, commentId);
        // 본인이 작성한 댓글이 아니면 에러
        isWriter(userName, comment);

        comment.setComment(commentRequest.getComment());
        return CommentResponse.of(comment);
    }

    public CommentDeleteResponse deleteComment(Long crewId, Long commentId, String userName) {
        Comment comment = checkCommentAndCrew(crewId, commentId);
        // 본인이 작성한 댓글이 아니면 에러
        isWriter(userName, comment);

        comment.deleteSoftly(LocalDateTime.now());
        commentRepository.deleteAll(comment.getChildren());
        return CommentDeleteResponse.of(commentId);
    }

    public CommentResponse getDetailComment(Long crewId, Long commentId) {
        Comment comment = checkCommentAndCrew(crewId, commentId);
        return CommentResponse.of(comment);
    }

    private Crew getCrew(Long crewId) {
        return crewRepository.findById(crewId).orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
    }

    private User getUser(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
    }
    private Comment getComment(Long parentCommentId) {
        return commentRepository.findById(parentCommentId).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }

    private Comment checkCommentAndCrew(Long crewId, Long commentId) {
        getCrew(crewId);
        return commentRepository.findById(commentId).orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }
    private void isWriter(String userName, Comment comment) {
        log.info("userName = {}, commentUser in Username = {}",userName, comment.getUser().getUsername());
        if (!comment.getUser().getUsername().equals(userName)) throw new AppException(ErrorCode.NOT_MATCH,ErrorCode.NOT_MATCH.getMessage());
    }

}
