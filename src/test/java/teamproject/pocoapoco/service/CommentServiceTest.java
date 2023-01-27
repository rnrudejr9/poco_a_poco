package teamproject.pocoapoco.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mockito;
import teamproject.pocoapoco.domain.dto.comment.CommentDeleteResponse;
import teamproject.pocoapoco.domain.dto.comment.CommentRequest;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.dto.user.UserJoinRequest;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.CommentEntityFixture;
import teamproject.pocoapoco.fixture.CrewEntityFixture;
import teamproject.pocoapoco.fixture.TestInfoFixture;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.AlarmRepository;
import teamproject.pocoapoco.repository.CommentRepository;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("댓글 서비스 테스트")
class CommentServiceTest {

    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CrewRepository crewRepository = Mockito.mock(CrewRepository.class);
    private AlarmRepository alarmRepository = Mockito.mock(AlarmRepository.class);
    private CommentService commentService;
    Comment commentEntity;
    private TestInfoFixture.TestInfo fixture;
    private CommentRequest commentRequest;
    private User userEntityFixture;
    private Crew crewEntityFixure;
    private String userName = "이름";
    private String password = "비밀번호";


    @BeforeEach
    void setUp() {
        commentEntity = CommentEntityFixture.get(userName, userName);
        commentService = new CommentService(commentRepository, userRepository, crewRepository, alarmRepository);
        userEntityFixture = UserEntityFixture.get(UserJoinRequest.builder()
                .userName(userName)
                .password(password)
                .build());
        crewEntityFixure = CrewEntityFixture.get(1L);
        fixture = TestInfoFixture.get();
        commentRequest = new CommentRequest("댓글작성");
    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class test {

        @Test
        @DisplayName("댓글 작성 성공")
        void success() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.save(any())).willReturn(commentEntity);

            //when
            CommentResponse result = commentService.addComment(commentRequest, fixture.getCrewId(), userName);

            //then
            assertDoesNotThrow(() -> commentService.addComment(commentRequest, fixture.getCrewId(), userName));
            assertEquals("comment 입니다.", result.getComment());
            assertEquals(commentEntity.getComment(), result.getComment());
            assertEquals(commentEntity.getId(), result.getId());
            assertEquals("이름", result.getUserName());
        }

        @Test
        @DisplayName("댓글 작성 실패 - 크루 모집 없음")
        void fail() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willThrow(new AppException(
                    ErrorCode.CREW_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
            given(commentRepository.save(any())).willReturn(commentEntity);

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.addComment(commentRequest, fixture.getCrewId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 작성 실패 - 유저이름 없음")
        void fail_1() {
            //given
            given(userRepository.findByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.save(any())).willReturn(commentEntity);

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.addComment(commentRequest, fixture.getCrewId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.USERID_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 작성 실패 - DB 에러")
        void fail_3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.save(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.addComment(commentRequest, fixture.getCrewId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(e.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }
    }
    @Nested
    @DisplayName("댓글 수정 테스트")
    class test2 {
        @Test
        @DisplayName("댓글 수정 성공")
        void success() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            CommentResponse result = commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), userName);

            //then
            assertDoesNotThrow(() -> commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), userName));
            assertEquals("댓글작성", result.getComment());
            assertEquals(commentEntity.getComment(), result.getComment());
            assertEquals(commentEntity.getId(), result.getId());
            assertEquals("이름", result.getUserName());
        }

        @Test
        @DisplayName("댓글 수정 실패 - 크루 모집 없음")
        void fail() {
            //given
            given(crewRepository.findById(any())).willThrow(new AppException(
                    ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 수정 실패 - comment 없음")
        void fail_1() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }
        @Test
        @DisplayName("댓글 수정 실패 - 작성자 불일치")
        void fail_2() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            String errorUserName = "ErrorUserName";
            AppException e = assertThrows(AppException.class, () -> commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), errorUserName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.NOT_MATCH);
            assertEquals(e.getMessage(), ErrorCode.NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("댓글 수정 실패 - DB 에러")
        void fail_3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.modifyComment(commentRequest, fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(e.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class test3 {
        @Test
        @DisplayName("댓글 삭제 성공")
        void success() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            CommentDeleteResponse result = commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), userName);

            //then
            assertDoesNotThrow(() -> commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), userName));
            assertEquals("댓글 삭제 완료", result.getMessage());
            assertEquals(commentEntity.getId(), result.getId());
        }

        @Test
        @DisplayName("댓글 삭제 실패 - 크루 모집 없음")
        void fail() {
            //given
            given(crewRepository.findById(any())).willThrow(new AppException(
                    ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 삭제 실패 - comment 없음")
        void fail_1() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }
        @Test
        @DisplayName("댓글 삭제 실패 - 작성자 불일치")
        void fail_2() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            String errorUserName = "ErrorUserName";
            AppException e = assertThrows(AppException.class, () -> commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), errorUserName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.NOT_MATCH);
            assertEquals(e.getMessage(), ErrorCode.NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("댓글 삭제 실패 - DB 에러")
        void fail_3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.deleteComment(fixture.getCrewId(), fixture.getCommentId(), userName));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(e.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 조회 테스트")
    class test4 {
        @Test
        @DisplayName("댓글 조회 성공")
        void success() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            CommentResponse result = commentService.getDetailComment(fixture.getCrewId(), fixture.getCommentId());

            //then
            assertDoesNotThrow(() -> commentService.getDetailComment(fixture.getCrewId(), fixture.getCommentId()));
            assertEquals("comment 입니다.", result.getComment());
            assertEquals(commentEntity.getId(), result.getId());
        }

        @Test
        @DisplayName("댓글 조회 실패 - 크루 모집 없음")
        void fail() {
            //given
            given(crewRepository.findById(any())).willThrow(new AppException(
                    ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
            given(commentRepository.findById(any())).willReturn(Optional.ofNullable(commentEntity));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.getDetailComment(fixture.getCrewId(), fixture.getCommentId()));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 조회 실패 - comment 없음")
        void fail_1() {
            //given
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.getDetailComment(fixture.getCrewId(), fixture.getCommentId()));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(e.getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 조회 실패 - DB 에러")
        void fail_3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(userEntityFixture));
            given(crewRepository.findById(any())).willReturn(Optional.of((crewEntityFixure)));
            given(commentRepository.findById(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException e = assertThrows(AppException.class, () -> commentService.getDetailComment(fixture.getCrewId(), fixture.getCommentId()));

            //then
            assertEquals(e.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(e.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }
    }


}