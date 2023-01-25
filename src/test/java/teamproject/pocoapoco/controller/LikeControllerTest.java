package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.service.LikeService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@Slf4j
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LikeService likeService;

    @Test
    @WithMockUser
    @DisplayName("좋아요 성공")
    public void good_Success() throws Exception {
        //given

        LikeResponse likeResponse = new LikeResponse("성공");
        //when
        when(likeService.goodCrew(any(),any())).thenReturn(likeResponse);

        //then
        mockMvc.perform(post("/api/v1/crews/2/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(likeResponse))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 취소")
    public void good_Cancel() throws Exception {
        //given
        LikeResponse likeResponse = new LikeResponse("취소");

        //when
        when(likeService.goodCrew(any(),any())).thenReturn(likeResponse);

        //then
        mockMvc.perform(post("/api/v1/crews/2/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(likeResponse))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 실패 - 로그인 안함")
    public void good_Fail_1() throws Exception {
        //given
        //when
        when(likeService.goodCrew(any(),any())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));

        //then
        mockMvc.perform(post("/api/v1/crews/2/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 실패 - 모임이 없음")
    public void good_Fail_2() throws Exception {
        //given
        //when
        when(likeService.goodCrew(any(),any())).thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));

        //then
        mockMvc.perform(post("/api/v1/crews/2/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 조회 성공")
    public void getLikeSuccess() throws Exception {
        //given
        //when
        when(likeService.getLike(any())).thenReturn(new LikeResponse("성공"));

        //then
        mockMvc.perform(get("/api/v1/crews/1/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(new LikeResponse("성공")));
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 조회 실패 : 해당 모임이 없음")
    public void getLikeFail() throws Exception {
        //given
        //when
        when(likeService.getLike(any())).thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));

        //then
        mockMvc.perform(get("/api/v1/crews/1/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1L)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }



}