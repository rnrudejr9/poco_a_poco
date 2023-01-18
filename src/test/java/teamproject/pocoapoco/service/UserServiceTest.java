package teamproject.pocoapoco.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.security.provider.JwtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Nested
    @DisplayName("로그인 Test")
    class Login {
        @Mock
        private UserRepository userRepository;

        @Mock
        private EncrypterConfig config;

        @Mock
        private JwtProvider jwtProvider;

        @InjectMocks
        UserService userService = new UserService(userRepository, config);


        @Value("${jwt.token.secret}") String secretKey;

        private User user;

        private UserLoginRequest userLoginRequest;

        private UserLoginResponse userLoginResponse;

        @BeforeEach
        void setup() {
            userLoginRequest = new UserLoginRequest("userId1234", "pass1234");

            userLoginResponse = new UserLoginResponse("token");

            user = User.builder()
                    .id(1L)
                    .userId("userId1234")
                    .userName("닉네임")
                    .password("pass1234")
                    .address("서울시 강남구")
                    .sport(InterestSport.SOCCER)
                    .manner(1)
                    .role(UserRole.ROLE_USER)
                    .build();
        }

        @Test
        @DisplayName("로그인 성공")
        public void 로그인테스트1() {

            //given
            when(userRepository.findByUserName(userLoginRequest.getUserId())).thenReturn(Optional.of(user));
            System.out.println(user.getUserId());
            System.out.println(userLoginRequest.getPassword());
            System.out.println(user.getPassword());

            when(config.encoder().matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtProvider.generateToken(user)).thenReturn("token");

            //when
            UserLoginResponse response = userService.login(userLoginRequest);

            //then
            assertThat(response.getJwt()).isEqualTo("token");

        }

        @Test
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        public void 로그인테스트2() {

            //given

            //when

            //then
        }

    }


}