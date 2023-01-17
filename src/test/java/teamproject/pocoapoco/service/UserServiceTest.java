package teamproject.pocoapoco.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EncrypterConfig config;






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
    public void 세팅(){

        when(config.encoder()).thenReturn(new BCryptPasswordEncoder());


    }


    @Test
    @DisplayName("회원가입 성공")
    public void 회원가입테스트1(){

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
    public void 회원가입테스트2(){
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