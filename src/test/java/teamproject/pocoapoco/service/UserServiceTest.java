package teamproject.pocoapoco.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EncrypterConfig config;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    UserService userService = new UserService(userRepository, config);


    @Value("${jwt.token.secret}") String secretKey;


    @Nested
    @DisplayName("로그인 Test")
    class Login {

        UserLoginRequest userLoginRequest = new UserLoginRequest("userId1234", "pass1234");

        UserLoginResponse userLoginResponse = new UserLoginResponse("token");


        User user = User.builder()
                .id(1L)
                .userId("userId1234")
                .userName("닉네임")
                .password("pass1234")
                .address("서울시 강남구")
                .sport(InterestSport.SOCCER)
                .manner(1)
                .role(UserRole.ROLE_USER)
                .build();

        @Test
        @DisplayName("로그인 성공")
        public void 로그인테스트1() {

            MockedStatic<JwtProvider> jwtTokenUtilMockedStatic = mockStatic(JwtProvider.class);

            given(userRepository.findByUserName(userLoginRequest.getUserId())).willReturn(Optional.of(user));
            given(config.encoder().matches(userLoginRequest.getPassword(), user.getPassword())).willReturn(true);
            given(jwtProvider.generateToken(user)).willReturn("token");

            UserLoginResponse response = userService.login(userLoginRequest);

            assertThat(response.getJwt()).isEqualTo("token");

            jwtTokenUtilMockedStatic.close();
//            //given
//            Mockito.lenient().when(userRepository.findByUserId(userLoginRequest.getUserId())).thenReturn(Optional.of(user));
//            //when(config.encoder().matches("123", "123")).thenReturn(true);
//            Mockito.lenient().when(jwtProvider.generateToken(user)).thenReturn("token");
//
//            //when
//            UserLoginResponse response = userService.login(userLoginRequest);
//
//            //then
//            assertEquals(response.getJwt(), "token");

        }

        @Test
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        public void 로그인테스트2() {

            //given
            Mockito.lenient().when(userRepository.findByUserId(userLoginRequest.getUserId())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            UserLoginResponse response = userService.login(userLoginRequest);

            //then
            assertEquals(HttpStatus.NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getHttpStatus());
        }

    }


    @Nested
    @DisplayName("회원가입 Test")
    class Join {

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


        // encoder 설정
        @BeforeEach
        public void 세팅() {

            when(config.encoder()).thenReturn(new BCryptPasswordEncoder());

        }


        @Test
        @DisplayName("회원가입 성공")
        public void 회원가입테스트1() {

            UserService service = new UserService(userRepository, config);

            User user1 = User.builder()
                    .id(1L)
                    .userId("아이디")
                    .userName("닉네임")
                    .password("비밀번호")
                    .address("서울시 강남구")
                    .sport(InterestSport.SOCCER)
                    .build();


            UserJoinResponse response = UserJoinResponse.builder()
                    .userId("아이디")
                    .message("회원가입 되었습니다.").build();


            when(userRepository.save(any())).thenReturn(user1);

            UserJoinResponse result = service.addUser(request1);

            assertAll(
                    () -> assertEquals(response.getUserId(), result.getUserId()),
                    () -> assertEquals(response.getMessage(), result.getMessage()));

        }

        @Test
        @DisplayName("회원가입 실패1 - 아이디 중복")
        public void 회원가입테스트2() {
            UserService service = new UserService(userRepository, config);

            User user1 = User.builder()
                    .id(1L)
                    .userId("아이디")
                    .userName("닉네임")
                    .password("비밀번호")
                    .address("서울시 강남구")
                    .sport(InterestSport.SOCCER)
                    .build();

            userRepository.save(user1);

            when(userRepository.save(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage()));

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                    () -> service.addUser(request2));

            assertEquals(exception.getMessage(), "이미 존재하는 아이디 입니다.");

        }

        @Test
        @DisplayName("회원가입 실패2 - 닉네임 중복")
        public void 회원가입테스트3() {

            UserService service = new UserService(userRepository, config);

            User user1 = User.builder()
                    .id(1L)
                    .userId("아이디")
                    .userName("닉네임")
                    .password("비밀번호")
                    .address("서울시 강남구")
                    .sport(InterestSport.SOCCER)
                    .build();

            userRepository.save(user1);

            when(userRepository.save(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                    () -> service.addUser(request2));

            assertEquals(exception.getMessage(), "이미 존재하는 닉네임 입니다.");
        }
    }



}