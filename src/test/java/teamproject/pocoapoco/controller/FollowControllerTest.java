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
import teamproject.pocoapoco.controller.main.api.FollowController;
import teamproject.pocoapoco.domain.dto.follow.FollowingResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.TestInfoFixture;
import teamproject.pocoapoco.service.FollowService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

//    @Test
//    @WithMockUser
//    @DisplayName("팔로잉")
//    public void followingSuccess() throws Exception {
//        //given
//        fixture = TestInfoFixture.get();
//        //when
//        when(followService.follow(any(),any())).thenReturn(fixture.getUserName()+"님을 팔로우 합니다.");
//
//        //then
//        mockMvc.perform(post("/api/v1/social/1/follow")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(1L)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value(fixture.getUserName()+"님을 팔로우 합니다."))
//                .andDo(print());
//    }
//    @Test
//    @WithMockUser
//    @DisplayName("언팔로잉")
//    public void unFollowingSuccess() throws Exception {
//        //given
//        fixture = TestInfoFixture.get();
//        //when
//        when(followService.follow(any(),any())).thenReturn(fixture.getUserName()+"님을 팔로우 취소 합니다.");
//
//        //then
//        mockMvc.perform(post("/api/v1/social/1/follow")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(1L)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value(fixture.getUserName()+"님을 팔로우 취소 합니다."))
//                .andDo(print());
//    }
    @Test
    @WithMockUser
    @DisplayName("팔로잉,언팔로잉 실패 - 해당 아이디 없음")
    void followFail1() throws Exception {

        when(followService.follow(any(), any()))
                .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

        mockMvc.perform(post("/api/v1/social/1/follow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                .andDo(print());
    }
    @Test
    @WithMockUser
    @DisplayName("팔로잉,언팔로잉 실패 - 본인 팔로잉/언팔로잉")
    void followFail2() throws Exception {

        when(followService.follow(any(), any()))
                .thenThrow(new AppException(ErrorCode.WRONG_PATH, ErrorCode.WRONG_PATH.getMessage()));

        mockMvc.perform(post("/api/v1/social/1/follow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(ErrorCode.WRONG_PATH.name() + " " + ErrorCode.WRONG_PATH.getMessage()))
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