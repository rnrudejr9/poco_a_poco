package teamproject.pocoapoco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import teamproject.pocoapoco.controller.main.api.UserController;
import teamproject.pocoapoco.domain.dto.user.*;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @MockBean
    UserRepository userRepository;
    UserLoginRequest request = new UserLoginRequest("userId1234", "pass1234");
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

            // given

            UserJoinResponse userJoinResponse = UserJoinResponse.builder()
                    .userId("아이디")
                    .message("회원가입 되었습니다.").build();

            given(userService.saveUser(any())).willReturn(userJoinResponse);

            // when
            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest1)))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.userId").value("아이디"))
                    .andExpect(jsonPath("$.result.message").value("회원가입 되었습니다."))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("회원가입 실패1 - 아이디 중복")
        public void 회원가입테스트2() throws Exception {

            // given

            userService.saveUser(userJoinRequest1);

            given(userService.saveUser(any())).willThrow(new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage()));

            // when
            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest2)))
                    // then
                    .andExpect(status().isConflict())
                    .andExpect(content().string(ErrorCode.DUPLICATED_USERID.name() + " 이미 존재하는 아이디 입니다."))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("회원가입 실패2 - 닉네임 중복")
        public void 회원가입테스트3() throws Exception {

            // given
            userService.saveUser(userJoinRequest1);
            given(userService.saveUser(any())).willThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));


            //when
            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest3)))
                    // then
                    .andExpect(status().isConflict())
                    .andExpect(content().string(ErrorCode.DUPLICATED_USERNAME.name() + " 이미 존재하는 닉네임 입니다."))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("회원가입 실패3 - 비밀번호 확인 실패")
        public void 회원가입테스트4() throws Exception {

            // given
            given(userService.saveUser(any())).willThrow(new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage()));

            // when
            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userJoinRequest4)))
                    //then
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.NOT_MATCH_PASSWORD.name() + " 패스워드가 일치하지 않습니다."))
                    .andDo(print());

        }

    }

    @Nested
    @DisplayName("로그인 Test")
    class Login {

        @Test
        @WithMockUser
        @DisplayName("로그인 성공")
        void 로그인테스트1() throws Exception {

            when(userService.login(any())).thenReturn(new UserLoginResponse("refreshToken","accessToken"));

            //when
            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                    .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                    .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        void 로그인테스트2() throws Exception {

            //given
            given(userService.login(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패2 - 비빌번호 불일치")
        void 로그인테스트3() throws Exception {

            //given
            given(userService.login(any())).willThrow(new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage()));

            //when
            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    //then
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_PASSWORD.name() + " " + ErrorCode.INVALID_PASSWORD.getMessage()))
                    .andDo(print());
        }
    }



    @Nested
    @DisplayName("프로필 정보 조회")
    class GetProfile{

        UserProfileResponse userProfileResponse = UserProfileResponse.builder()
                .userName("닉네임")
                .address("주소")
                .likeSoccer(true)
                .likeJogging(true)
                .likeTennis(false)
                .build();

        @Test
        @WithMockUser
        @DisplayName("프로필 조회 성공 - 내 프로필")
        void 프로필조회1() throws Exception {

            //given
            given(userService.getUserInfoByUserName(any())).willReturn(userProfileResponse);

            //when
            mockMvc.perform(get("/api/v1/users/profile/my")
                            .with(csrf()))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.userName").value("닉네임"))
                    .andExpect(jsonPath("$.result.likeSoccer").value(true))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("프로필 조회 실패1 - 사용자 정보 없음")
        void 프로필조회2() throws Exception {

            //given
            given(userService.getUserInfoByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));


            //when
            mockMvc.perform(get("/api/v1/users/profile/my")
                            .with(csrf()))
                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " " + ErrorCode.USERID_NOT_FOUND.getMessage()))
                    .andDo(print());

        }



    }


    @Nested
    @DisplayName("프로필 정보 수정")
    class ReviseProfile{


        UserProfileRequest userProfileRequest1 = UserProfileRequest.builder()
                .userName("닉네임")
                .address("주소")
                .password("비밀번호")
                .passwordConfirm("비밀번호")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(true)
                .build();

        UserProfileRequest userProfileRequest2 = UserProfileRequest.builder()
                .userName("닉네임")
                .address("주소")
                .password("비밀번호")
                .passwordConfirm("비밀번호123")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(true)
                .build();

        UserProfileResponse userProfileResponse1 = UserProfileResponse.builder()
                .userName("닉네임")
                .address("주소")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(true)
                .build();

        @Test
        @WithMockUser
        @DisplayName("프로필 수정 성공")
        void 프로필수정1() throws Exception {

            //given
            given(userService.updateUserInfoByUserName(any(), any()))
                    .willReturn(userProfileResponse1);



            // when
            mockMvc.perform(put("/api/v1/users/revise")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest1)))
                    // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.userName").value("닉네임"))
                    .andExpect(jsonPath("$.result.address").value("주소"))
                    .andDo(print());
        }


        @Test
        @WithMockUser
        @DisplayName("프로필 수정 실패1 - 비밀번호 확인 실패")
        void 프로필수정2() throws Exception {
            // given: 전역변수
            given(userService.updateUserInfoByUserName(any(), any()))
                    .willThrow(new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage()));




            //when
            mockMvc.perform(put("/api/v1/users/revise")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest2)))
                    //then
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.NOT_MATCH_PASSWORD.name() + " 패스워드가 일치하지 않습니다."))
                    .andDo(print());
        }


        @Test
        @WithMockUser
        @DisplayName("프로필 수정 실패2 - 사용자를 찾지 못함")
        void 프로필수정3() throws Exception {

            // given:
            given(userService.updateUserInfoByUserName(any(), any()))
                    .willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(put("/api/v1/users/revise")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest2)))
                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " 아이디가 존재하지 않습니다."))
                    .andDo(print());


        }



    }




}