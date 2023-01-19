package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.UserRepository;
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

    // Autowired를

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Nested
    @DisplayName("회원가입 테스트")
    public class JoinTest{

        UserJoinRequest userJoinRequest1 = new UserJoinRequest("아이디", "닉네임", "비밀번호", "비밀번호", "서울시 강남구", true, false, false);
        UserJoinRequest userJoinRequest2 = new UserJoinRequest("아이디", "닉네임11", "비밀번호11", "비밀번호11", "서울시 강남구123", true, false, true);
        UserJoinRequest userJoinRequest3 = new UserJoinRequest("아이디12", "닉네임", "비밀번호입니다", "비밀번호입니다", "서울시 강남구 신사동", true, false, false);
        UserJoinRequest userJoinRequest4 = new UserJoinRequest("아이디12", "닉네임", "비밀번호입니다", "비밀번호입니다123", "서울시 강남구 신사동", true, false, false);


        User user1 = UserEntityFixture.get(userJoinRequest1);


        @Test
        @WithMockUser
        @DisplayName("회원가입 성공")
        public void 회원가입테스트1() throws Exception {

            UserJoinResponse userJoinResponse = UserJoinResponse.builder()
                    .userId("아이디")
                    .message("회원가입 되었습니다.").build();


            when(userService.addUser(any()))
                    .thenReturn(userJoinResponse);

            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value("아이디"))
                    .andExpect(jsonPath("$.message").value("회원가입 되었습니다."))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("회원가입 실패1 - 아이디 중복")
        public void 회원가입테스트2() throws Exception {

            userService.addUser(userJoinRequest1);

            when(userService.addUser(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage()));

            // runtime exception을 throw 할 때 bad request가 발생하도록 설정 -> 4XX exception이 발생하는지 확인

            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest2)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(ErrorCode.DUPLICATED_USERID.name() + " 이미 존재하는 아이디 입니다."))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("회원가입 실패2 - 닉네임 중복")
        public void 회원가입테스트3() throws Exception {

            userService.addUser(userJoinRequest1);

            when(userService.addUser(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));

            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest3)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(ErrorCode.DUPLICATED_USERNAME.name() + " 이미 존재하는 닉네임 입니다."))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("회원가입 실패3 - 비밀번호 확인 실패")
        public void 회원가입테스트4() throws Exception {


            when(userService.addUser(any())).thenThrow(new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage()));

            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest4)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.NOT_MATCH_PASSWORD.name() + " 패스워드가 일치하지 않습니다."))
                    .andDo(print());

        }

    }

    @Nested
    @DisplayName("로그인 Test")
    class Login {

        UserLoginRequest userLoginRequest = new UserLoginRequest("userId1234", "pass1234");


        @Test
        @WithMockUser
        @DisplayName("로그인 성공")
        void 로그인테스트1() throws Exception {

            //given
            when(userService.login(any())).thenReturn(new UserLoginResponse("token"));

            //when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                    .andDo(print());

            //then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.jwt").value("token"))
                    .andDo(print());


        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        void 로그인테스트2() throws Exception {

            //given
            when(userService.login(any())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                    .andDo(print());

            //then
            resultActions
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

            //when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                    .andDo(print());

            //then
            resultActions
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_PASSWORD.name() + " " + ErrorCode.INVALID_PASSWORD.getMessage()))
                    .andDo(print());
        }
    }


}