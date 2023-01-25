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
import teamproject.pocoapoco.domain.dto.user.UserLoginRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginResponse;
import teamproject.pocoapoco.domain.dto.user.UserProfileRequest;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.security.provider.JwtProvider;
import teamproject.pocoapoco.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    UserLoginRequest request = new UserLoginRequest("userId1234", "pass1234");

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

        JwtProvider jwtProvider = new JwtProvider();

        @Test
        @WithMockUser
        @DisplayName("프로필 조회 성공 - 내 프로필")
        void 프로필조회1() throws Exception {

            //given
            when(userService.selectUserInfo(any())).thenReturn(userProfileResponse);

            //when
            mockMvc.perform(get("/api/v1/users/profile/my")
                            .header("Authorization", "Bearer token.toke2n.token")
                            .with(csrf()))
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
            when(jwtProvider.getId(any())).thenReturn(1L);
            when(userService.selectUserInfo(any())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            mockMvc.perform(get("/api/v1/users/profile/my")
                            .header("Authorization", "Bearer token.token.token")
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
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

            //given: 전역변수

            // when
            when(userService.modifyMyUserInfo(any(), any()))
                    .thenReturn(userProfileResponse1);

            // then
            mockMvc.perform(put("/api/v1/users/revise")
                            .header("Authorization", "Bearer token.token.token")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest1)))
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

            // when
            when(userService.modifyMyUserInfo(any(), any()))
                    .thenThrow(new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage()));

            //then
            mockMvc.perform(put("/api/v1/users/revise")
                            .header("Authorization", "Bearer token.token.token")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest2)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.NOT_MATCH_PASSWORD.name() + " 패스워드가 일치하지 않습니다."))
                    .andDo(print());


        }

        @Test
        @WithMockUser
        @DisplayName("프로필 수정 실패2 - 유효하지 않은 토큰")
        void 프로필수정3() throws Exception {

            // given: 전역변수


            // when
            //when(jwtProvider.validateToken(anyString())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage()));
            when(userService.modifyMyUserInfo(any(), any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage()));

            //then
            mockMvc.perform(put("/api/v1/users/revise")
                            .header("Authorization", "Bearer token.token.token")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest1)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(ErrorCode.INVALID_TOKEN.name() + " 잘못된 토큰입니다."))
                    .andDo(print());

        }


        @Test
        @WithMockUser
        @DisplayName("프로필 수정 실패3 - 사용자를 찾지 못함")
        void 프로필수정4() throws Exception {

            // given: 전역변수

            // when
            when(userService.modifyMyUserInfo(any(), any()))
                    .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //then
            mockMvc.perform(put("/api/v1/users/revise")
                            .header("Authorization", "Bearer token.token.token")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userProfileRequest2)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(ErrorCode.USERID_NOT_FOUND.name() + " 아이디가 존재하지 않습니다."))
                    .andDo(print());


        }



    }




}