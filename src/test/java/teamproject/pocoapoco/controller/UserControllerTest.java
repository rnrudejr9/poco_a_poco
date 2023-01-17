package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@Slf4j
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    // 전역 변수 선언
    UserJoinRequest request1 = UserJoinRequest.builder()
            .userId("아이디")
            .userName("닉네임")
            .password("비밀번호")
            .address("서울시 강남구")
            .likeSoccer(true)
            .likeJogging(false)
            .likeTennis(false)
            .build();

    UserJoinRequest request2 = UserJoinRequest.builder()
            .userId("아이디")
            .userName("닉네임11")
            .password("비밀번호11")
            .address("서울시 강남구123")
            .likeSoccer(true)
            .likeJogging(false)
            .likeTennis(false)
            .build();

    UserJoinRequest request3 = UserJoinRequest.builder()
            .userId("아이디12")
            .userName("닉네임")
            .password("비밀번호입니다")
            .address("서울시 강남구 신사동")
            .likeSoccer(true)
            .likeJogging(false)
            .likeTennis(false)
            .build();





    @Test
    @WithMockUser
    @DisplayName("회원가입 성공")
    public void 회원가입테스트1() throws Exception {

        UserJoinResponse response = UserJoinResponse.builder()
                .userId("아이디")
                .message("회원가입 되었습니다.").build();


        when(userService.addUser(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("아이디"))
                .andExpect(jsonPath("$.message").value("회원가입 되었습니다."))
                .andDo(print());


    }


    @Test
    @WithMockUser
    @DisplayName("회원가입 실패1 - 아이디 중복")
    public void 회원가입테스트2() throws Exception {

        userService.addUser(request1);

        when(userService.addUser(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage()));

        // runtime exception을 throw 할 때 bad request가 발생하도록 설정 -> 4XX exception이 발생하는지 확인

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request2)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(ErrorCode.DUPLICATED_USERID.name() + " 이미 존재하는 아이디 입니다."))
                .andDo(print());


    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패2 - 닉네임 중복")
    public void 회원가입테스트3() throws Exception {

        userService.addUser(request1);

        when(userService.addUser(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request3)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(ErrorCode.DUPLICATED_USERNAME.name() + " 이미 존재하는 닉네임 입니다."))
                .andDo(print());

    }



}