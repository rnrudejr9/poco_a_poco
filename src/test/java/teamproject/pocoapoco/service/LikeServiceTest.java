package teamproject.pocoapoco.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.CrewEntityFixture;
import teamproject.pocoapoco.fixture.LikeEntityFixture;
import teamproject.pocoapoco.fixture.TestInfoFixture;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.LikeRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    LikeService likeService;

    LikeRepository likeRepository = mock(LikeRepository.class);
    CrewRepository crewRepository = mock(CrewRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    User user;
    Crew crew;

    @BeforeEach
    void setUp() {

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

        likeService = new LikeService(likeRepository,crewRepository,userRepository);
        user = UserEntityFixture.get(userJoinRequest1);
        crew = CrewEntityFixture.get(TestInfoFixture.get().getUserName(),TestInfoFixture.get().getPassword());
    }

    @Test
    @DisplayName("좋아요 성공")
    @WithMockUser
    void goodSuccess(){
//        given

//        when
        when(userRepository.findByUserName(user.getUsername())).thenReturn(Optional.of(user));
        when(crewRepository.findById(crew.getId())).thenReturn(Optional.of(crew));
        user.getLikes().add(Like.builder().user(user).crew(crew).build());

//        then
        Assertions.assertDoesNotThrow(() -> likeService.goodCrew(crew.getId(),user.getUsername()));
    }

    @Test
    @DisplayName("좋아요 취소")
    @WithMockUser
    void goodCancel(){
//        given

//        when
        when(userRepository.findByUserName(user.getUsername())).thenReturn(Optional.of(user));
        when(crewRepository.findById(crew.getId())).thenReturn(Optional.of(crew));
        user.getLikes().add(Like.builder().user(user).crew(crew).build());

//        then
        Assertions.assertDoesNotThrow(() -> likeService.goodCrew(crew.getId(),user.getUsername()));
    }

    @Test
    @DisplayName("좋아요 실패 - 로그인 하지않음")
    @WithMockUser
    void goodFail(){

        when(userRepository.findByUserName(user.getUsername())).thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        RuntimeException exception = Assertions.assertThrows(AppException.class,
                () -> likeService.goodCrew(crew.getId(),user.getUsername()));
        assertEquals(exception.getMessage(),ErrorCode.USERID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("좋아요 실패 - 게시글 없음")
    @WithMockUser
    void goodFail2(){
        when(userRepository.findByUserName(user.getUsername())).thenReturn(Optional.of(user));
        when(crewRepository.findById(crew.getId())).thenThrow(new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        RuntimeException exception = Assertions.assertThrows(AppException.class,
                () -> likeService.goodCrew(crew.getId(),user.getUsername()));
        assertEquals(exception.getMessage(),ErrorCode.CREW_NOT_FOUND.getMessage());
    }

}