package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    UserLoginRequest request = new UserLoginRequest("userId1234", "pass1234");

    @Nested
    @DisplayName("로그인 Test")
    class Login {

        @Test
        @WithMockUser
        @DisplayName("로그인 성공")
        void 로그인테스트1() throws Exception {

            when(userService.login(any())).thenReturn(new UserLoginResponse("token"));

            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.jwt").value("token"))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        void 로그인테스트2() throws Exception {

            when(userService.login(any())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패2 - 비빌번호 불일치")
        void 로그인테스트3() throws Exception {

            //given
            when(userService.login(any())).thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage()));

            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_PASSWORD.name() + " " + ErrorCode.INVALID_PASSWORD.getMessage()))
                    .andDo(print());
        }
    }

}