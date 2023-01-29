package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.controller.main.api.CommentController;
import teamproject.pocoapoco.domain.dto.comment.CommentDeleteResponse;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.CommentEntityFixture;
import teamproject.pocoapoco.fixture.TestInfoFixture;
import teamproject.pocoapoco.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("댓글 테스트")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    private TestInfoFixture.TestInfo fixture;
    private String commentRequest;
    private CommentResponse commentResponse;
    private CommentDeleteResponse commentDeleteResponse;

    @BeforeEach()
    void setUp() {
        fixture = TestInfoFixture.get();
        commentResponse = new CommentResponse(fixture.getCommentId(),"댓글",fixture.getUserName(), fixture.getCrewId(),LocalDateTime.now());
        commentDeleteResponse = new CommentDeleteResponse("댓글이 삭제되엇습니다.", fixture.getCommentId());
        commentRequest = "댓글 작성 테스트";
    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class test {

        @Test
        @DisplayName("댓글 작성 성공")
        @WithMockUser
        void succecc() throws Exception {
            //given
            given(commentService.addComment(any(), any(), any()))
                    .willReturn(commentResponse);

            //when
            mockMvc.perform(post("/api/v1/crews/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.comment").value("댓글"))
            ;
        }

        @Test
        @DisplayName("댓글 작성 실패 - 로그인 되어잇지 않음")
        @WithAnonymousUser
        void fail_1() throws Exception {

            //when
            mockMvc.perform(post("/api/v1/crews/1/comments")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        @DisplayName("댓글 작성 실패 - crew없음")
        @WithMockUser
        void fail_2() throws Exception {
            //given
            given(commentService.addComment(any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(post("/api/v1/crews/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("CREW_NOT_FOUND 해당 모임이 없습니다."))
            ;
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class test2 {

        @Test
        @WithMockUser
        @DisplayName("댓글 수정 성공")
        void success() throws Exception {
            //given
            given(commentService.modifyComment(any(), any(), any(), any()))
                    .willReturn(commentResponse);


            //when
            mockMvc.perform(put("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.comment").value("댓글"))
            ;
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 수정 실패 - 로그인 되어있지 않음")
        void fail_1() throws Exception {

            //when
            mockMvc.perform(post("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        @DisplayName("댓글 수정 실패 - crew없음")
        @WithMockUser
        void fail_2() throws Exception {
            //given
            given(commentService.modifyComment(any(), any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(put("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("CREW_NOT_FOUND 해당 모임이 없습니다."))
            ;
        }

        @Test
        @DisplayName("댓글 수정 실패 - 작성자 불일치")
        @WithMockUser
        void fail_3() throws Exception {
            //given
            given(commentService.modifyComment(any(), any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(put("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("USERID_NOT_FOUND 아이디가 존재하지 않습니다."))
            ;
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class test3 {

        @Test
        @WithMockUser
        @DisplayName("댓글 삭제 성공")
        void success() throws Exception {
            //given
            given(commentService.deleteComment(any(), any(), any()))
                    .willReturn(commentDeleteResponse);


            //when
            mockMvc.perform(delete("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("댓글이 삭제되엇습니다."))
                    .andExpect(jsonPath("$.result.id").value(1))
            ;
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 삭제 실패 - 로그인 되어있지 않음")
        void fail_1() throws Exception {

            //when
            mockMvc.perform(delete("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        @DisplayName("댓글 삭제 실패 - crew없음")
        @WithMockUser
        void fail_2() throws Exception {
            //given
            given(commentService.deleteComment(any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(delete("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("CREW_NOT_FOUND 해당 모임이 없습니다."))
            ;
        }

        @Test
        @DisplayName("댓글 삭제 실패 - 작성자 불일치")
        @WithMockUser
        void fail_3() throws Exception {
            //given
            given(commentService.deleteComment(any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(delete("/api/v1/crews/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest)))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("USERID_NOT_FOUND 아이디가 존재하지 않습니다."))
            ;
        }
    }

    @Nested
    @DisplayName("댓글 한개 조회")
    class test4 {

        @Test
        @WithMockUser
        @DisplayName("댓글 상세조회 성공")
        void success() throws Exception {
            //given
            given(commentService.getDetailComment(any(), any()))
                    .willReturn(commentResponse);

            //when
            mockMvc.perform(get("/api/v1/crews/1/comments/1")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.userName").value("name"))
                    .andExpect(jsonPath("$.result.comment").value("댓글"))
            ;
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 상세조회 실패 - 로그인 되어있지 않음")
        void fail_1() throws Exception {
            //when
            mockMvc.perform(get("/api/v1/crews/1/comments/1")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        @WithMockUser
        @DisplayName("댓글 상세조회 실패 - crew 없음")
        void fail_2() throws Exception {
            //given
            given(commentService.getDetailComment(any(), any()))
                    .willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(get("/api/v1/crews/1/comments/1")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("CREW_NOT_FOUND 해당 모임이 없습니다."))
            ;
        }

        @Test
        @WithMockUser
        @DisplayName("댓글 상세조회 실패 - comment 없음")
        void fail_3() throws Exception {
            //given
            given(commentService.getDetailComment(any(), any()))
                    .willThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(get("/api/v1/crews/1/comments/1")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("COMMENT_NOT_FOUND 해당 댓글이 없습니다."))
            ;
        }
    }

    @Nested
    @DisplayName("댓글 리스트")
    class test5 {
        @Test
        @WithMockUser
        @DisplayName("댓글 리스트 조회 - 성공")
        void success() throws Exception {
            //given
            List<CommentResponse> list = List.of(CommentResponse.of(
                    CommentEntityFixture.get("userName123", "password")
            ));
            Page<CommentResponse> responses = new PageImpl<>(list);
            given(commentService.getCommentList(any(), any())).willReturn(responses);

            //when
            mockMvc.perform(get("/api/v1/crews/1/comments")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$['result']['content'][0]['userName']").value("userName123"));
        }

        @Test
        @WithMockUser
        @DisplayName("댓글 리스트 조회실패 - crew 없음")
        void fail_1() throws Exception {
            //given
            given(commentService.getCommentList(any(), any())).willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(get("/api/v1/crews/1/comments")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("CREW_NOT_FOUND 해당 모임이 없습니다."));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 리스트 조회실패 - 로그인 하지 않음")
        void fail_2() throws Exception {
            //when
            mockMvc.perform(get("/api/v1/crews/1/comments")
                            .with(csrf()))
                    //then
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

    }
}