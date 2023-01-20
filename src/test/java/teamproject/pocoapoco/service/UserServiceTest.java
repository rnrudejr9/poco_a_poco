package teamproject.pocoapoco.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.security.provider.JwtProvider;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Nested
    @DisplayName("회원가입 테스트")
    public class JoinTest{

        @Mock
        UserRepository userRepository;

        @Mock
        EncrypterConfig config;

        @Mock
        private JwtProvider jwtProvider;

        UserJoinRequest userJoinRequest1 = UserJoinRequest.builder()
                .userId("아이디")
                .userName("닉네임")
                .password("비밀번호")
                .passwordConfirm("비밀번호")
                .address("서울시 강남구")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(false)
                .build();
        UserJoinRequest userJoinRequest2 = UserJoinRequest.builder()
                .userId("아이디")
                .userName("닉네임312")
                .password("비밀번호")
                .passwordConfirm("비밀번호")
                .address("서울시 강남구")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(false)
                .build();

        UserJoinRequest userJoinRequest3 = UserJoinRequest.builder()
                .userId("아이디12")
                .userName("닉네임")
                .password("비밀번호")
                .passwordConfirm("비밀번호")
                .address("서울시 강남구")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(false)
                .build();

        UserJoinRequest userJoinRequest4 = UserJoinRequest.builder()
                .userId("아이디12")
                .userName("닉네임")
                .password("비밀번호")
                .passwordConfirm("비밀번호wex")
                .address("서울시 강남구")
                .likeSoccer(true)
                .likeJogging(false)
                .likeTennis(false)
                .build();

        User user1 = UserEntityFixture.get(userJoinRequest1);

        UserService userService;

        private UserLoginResponse userLoginResponse;

        // encoder 설정
        @BeforeEach
        public void 세팅(){

            when(config.encoder()).thenReturn(new BCryptPasswordEncoder());
            userService = new UserService(config, userRepository);


        }


        @Test
        @DisplayName("회원가입 성공")
        public void 회원가입테스트1() {

            //given
            UserJoinResponse userJoinResponse = UserJoinResponse.builder()
                    .userId("아이디")
                    .message("회원가입 되었습니다.").build();


            // when
            when(userRepository.save(any())).thenReturn(user1);
            UserJoinResponse result = userService.addUser(userJoinRequest1);

            // then
            assertAll(
                    () -> assertEquals(userJoinResponse.getUserId(), result.getUserId()),
                    () -> assertEquals(userJoinResponse.getMessage(), result.getMessage()));
        }



        @Test
        @DisplayName("회원가입 실패1 - 아이디 중복")
        public void 회원가입테스트2(){
            // given
            userRepository.save(user1);


            //when
            when(userRepository.save(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage()));
            RuntimeException exception = Assertions.assertThrows(AppException.class,
                    () -> userService.addUser(userJoinRequest2));

            //then
            assertEquals(exception.getMessage(), "이미 존재하는 아이디 입니다.");

        }

        @Test
        @DisplayName("회원가입 실패2 - 닉네임 중복")
        public void 회원가입테스트3() {


            //given
            userRepository.save(user1);


            // when
            when(userRepository.save(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));
            RuntimeException exception = Assertions.assertThrows(AppException.class,
                    () -> userService.addUser(userJoinRequest3));

            //then
            assertEquals(exception.getMessage(), "이미 존재하는 닉네임 입니다.");
        }

        @Test
        @DisplayName("회원가입 실패3 - 확인용 비밀번호가 비밀번호와 일치하지 않음")
        public void 회원가입테스트4() {

            //given: userJoinRequest4


            //when
            lenient().when(userRepository.save(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage()));
            RuntimeException exception = Assertions.assertThrows(AppException.class,
                    () -> userService.addUser(userJoinRequest4));

            //then
            assertEquals(exception.getMessage(), "패스워드가 잘못되었습니다.");
        }

    }

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
        UserService userService = new UserService(config, userRepository);

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
                    .sport(Sport.setSport(true, false, false))
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