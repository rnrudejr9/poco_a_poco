package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.domain.dto.follow.FollowingResponse;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.TestInfoFixture;
import teamproject.pocoapoco.service.CommentService;
import teamproject.pocoapoco.service.FollowService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("팔로우 테스트")
@WebMvcTest(FollowController.class)
class FollowControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    FollowService followService;
    private TestInfoFixture.TestInfo fixture;
    private FollowingResponse followingResponse;
    @Test
    @WithMockUser
    @DisplayName("팔로잉")
    public void following_Success() throws Exception {
        //given
        fixture = TestInfoFixture.get();
        //when
        when(followService.follow(any(),any())).thenReturn(fixture.getUserName()+"님을 팔로우 합니다.");

        //then
        mockMvc.perform(post("/api/v1/social/1/follow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(fixture.getUserName()+"님을 팔로우 합니다."))
                .andDo(print());
    }
    @Test
    @WithMockUser
    @DisplayName("언팔로잉")
    public void unFollowing_Success() throws Exception {
        //given
        fixture = TestInfoFixture.get();
        //when
        when(followService.follow(any(),any())).thenReturn(fixture.getUserName()+"님을 팔로우 취소 합니다.");

        //then
        mockMvc.perform(post("/api/v1/social/1/follow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(fixture.getUserName()+"님을 팔로우 취소 합니다."))
                .andDo(print());
    }
    @Test
    @WithMockUser
    @DisplayName("팔로잉 수")
    public void followingCount() throws Exception {
        //given
        fixture = TestInfoFixture.get();
        //when
        when(followService.followingCount(any())).thenReturn(0);

        //then
        mockMvc.perform(get("/api/v1/social/1/followingCount")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0))
                .andDo(print());
    }
    @Test
    @WithMockUser
    @DisplayName("팔로워 수")
    public void followedCount() throws Exception {
        //given
        fixture = TestInfoFixture.get();
        //when
        when(followService.followingCount(any())).thenReturn(0);

        //then
        mockMvc.perform(get("/api/v1/social/1/followedCount")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0))
                .andDo(print());
    }

}