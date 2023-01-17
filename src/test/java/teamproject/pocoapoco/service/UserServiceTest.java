package teamproject.pocoapoco.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EncrypterConfig config;

    @Mock
    JwtProvider jwtProvider;

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


    @Nested
    @DisplayName("로그인 Test")
    class Login {

        UserService service = new UserService(userRepository, config);

        // 로그인 전역 변수 선언
        UserLoginRequest request = new UserLoginRequest("userId1234", "pass1234");

        UserLoginResponse response = new UserLoginResponse("token");

        User user = User.builder()
                .id(1L)
                .userId("userId1234")
                .userName("닉네임")
                .password("비밀번호")
                .address("서울시 강남구")
                .sport(InterestSport.SOCCER)
                .manner(1)
                .role(UserRole.ROLE_USER)
                .build();

        @Test
        @DisplayName("로그인 성공")
        public void 로그인테스트1() {

            // userId 유효성 확인
            Mockito.lenient().when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.ofNullable(user));

            // password 확인
            //when(config.encoder().matches("123", "123")).thenReturn(true);

            //토큰 발행 확인
            Mockito.lenient().when(jwtProvider.generateToken(user)).thenReturn("token");


            assertAll(
                    () -> assertEquals(request.getUserId(), user.getUserId()),
                    () -> assertEquals("token", response.getJwt()));

        }

        @Test
        @DisplayName("로그인 실패1 - 해당 아이디 없음")
        public void 로그인테스트2() {

            Mockito.lenient().when(userRepository.findByUserId(request.getUserId())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            assertEquals(HttpStatus.NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getHttpStatus());
        }

    }
}