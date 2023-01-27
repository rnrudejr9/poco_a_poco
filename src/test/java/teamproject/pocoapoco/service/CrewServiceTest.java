package teamproject.pocoapoco.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewStrictRequest;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.fixture.CrewEntityFixture;
import teamproject.pocoapoco.fixture.UserEntityFixture;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CrewServiceTest {

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CrewService crewService;

    Crew crew = CrewEntityFixture.get(1L);
    Page<Crew> crewPage = new PageImpl<>(List.of(crew));
    CrewRequest request = new CrewRequest(crew.getStrict(), crew.getTitle(), crew.getContent(), crew.getCrewLimit());
    CrewDetailResponse crewDetailResponse = CrewDetailResponse.of(CrewEntityFixture.get(crew.getId()));

    User user = User.builder()
            .id(1L)
            .userId("userId")
            .userName("dokim")
            .password("pass1234")
            .address("서울시 강남구")
            .sport(Sport.setSport(true, false, false))
            .manner(1)
            .role(UserRole.ROLE_USER)
            .build();

    @Nested
    @DisplayName("크루 게시글 등록")
    public class AddCrew {
        @Test
        @DisplayName("크루 게시글 등록 성공")
        void addCrew1() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.save(any())).willReturn(crew);

            //when
            CrewResponse response = crewService.addCrew(request, user.getUsername());

            //then
            assertEquals("Crew 등록 완료", response.getMessage());
            assertEquals(1L, response.getCrewId());
        }

        @Test
        @DisplayName("크루 게시글 등록 실패1 : 해당 아이디 없음 ")
        void addCrew2() {
            //given
            given(userRepository.findByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.addCrew(request, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USERID_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 등록 실패2 : DB 에러 ")
        void addCrew3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.save(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.addCrew(request, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(exception.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }

    }

    @Nested
    @DisplayName("크루 게시글 수정")
    public class ModifyCrew {
//        @Test
//        @DisplayName("크루 게시글 수정 성공")
//        void modifyCrew1() {
//            //given
//            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
//            given(crewRepository.findById(any())).willReturn(Optional.ofNullable(crew));
//
//            //when
//            CrewResponse response = crewService.modifyCrew(1L, request, user.getUsername());
//
//            //then
//            assertEquals("Crew 수정 완료", response.getMessage());
//            assertEquals(1L, response.getCrewId());
//        }

        @Test
        @DisplayName("크루 게시글 수정 실패1 : 해당 아이디 없음 ")
        void modifyCrew2() {
            //given
            given(userRepository.findByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.modifyCrew(1L, request, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USERID_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 수정 실패2 : 해당 게시글 없음")
        void modifyCrew3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.findById(any())).willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.modifyCrew(1L, request, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 수정 실패3 : DB 에러 ")
        void modifyCrew4() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.save(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.addCrew(request, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(exception.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }

    }

    @Nested
    @DisplayName("크루 게시글 삭제")
    public class DeleteCrew {

//        @Test
//        @DisplayName("크루 게시글 삭제 성공")
//        void deleteCrew1() {
//            //given
//            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
//            given(crewRepository.findById(any())).willReturn(Optional.ofNullable(crew));
//
//            //when
//            CrewResponse response = crewService.deleteCrew(1L, user.getUsername());
//
//            //then
//            assertEquals("Crew 수정 완료", response.getMessage());
//            assertEquals(1L, response.getCrewId());
//        }

        @Test
        @DisplayName("크루 게시글 삭제 실패1 : 해당 아이디 없음 ")
        void deleteCrew2() {
            //given
            given(userRepository.findByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.deleteCrew(1L, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USERID_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 삭제 실패2 : 해당 게시글 없음")
        void deleteCrew3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.findById(any())).willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.deleteCrew(1L, user.getUsername()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

//        @Test
//        @DisplayName("크루 게시글 삭제 실패3 : DB 에러 ")
//        void deleteCrew4() {
//            //given
//            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
//            given(crewRepository.findById(any())).willReturn(Optional.ofNullable(crew));
//            crew.deleteSoftly(LocalDateTime.now());
//            given(user.getCrews().contains(crew)).willReturn(true);
//            given(crewRepository.save(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));
//
//            //when
//            AppException exception = assertThrows(AppException.class, () -> crewService.deleteCrew(1L, user.getUsername()));
//
//            //then
//            assertEquals(exception.getErrorCode(), ErrorCode.DB_ERROR);
//            assertEquals(exception.getMessage(), ErrorCode.DB_ERROR.getMessage());
//        }
    }

    @Nested
    @DisplayName("크루 게시글 상세조회")
    public class DetailCrew {
        @Test
        @DisplayName("크루 게시글 상세조회 성공")
        void detailCrew1() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.findById(any())).willReturn(Optional.ofNullable(crew));

            //when
            CrewDetailResponse response = crewService.detailCrew(crew.getId());

            //then
            assertEquals(crew.getStrict(), response.getStrict());
            assertEquals(crew.getTitle(), response.getTitle());
            assertEquals(crew.getContent(), response.getContent());
            assertEquals(crew.getCrewLimit(), response.getCrewLimit());
        }

        @Test
        @DisplayName("크루 게시글 상세조회 실패1 : 해당 아이디 없음")
        void detailCrew2() {
            //given
            given(userRepository.findByUserName(any())).willThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.detailCrew(crew.getId()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USERID_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 상세조회 실패2 : 해당 게시글 없음")
        void detailCrew3() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.findById(any())).willThrow(new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.detailCrew(crew.getId()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.CREW_NOT_FOUND);
            assertEquals(exception.getMessage(), ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("크루 게시글 상세조회 실패3 : DB 에러")
        void detailCrew4() {
            //given
            given(userRepository.findByUserName(any())).willReturn(Optional.ofNullable(user));
            given(crewRepository.findById(any())).willReturn(Optional.ofNullable(crew));
            given(crewRepository.findById(any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.detailCrew(crew.getId()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(exception.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }

    }


    @Nested
    @DisplayName("크루 게시글 전체조회")
    public class FindAllCrews {
        @Test
        @DisplayName("크루 게시글 전체조회 성공")
        void findAllCrews1() {
            //given
            given(crewRepository.findAll((Pageable) any())).willReturn(crewPage);

            //when
            Page<CrewDetailResponse> response = crewService.findAllCrews(any());

            //then
            assertEquals(1, response.getTotalElements());
            assertEquals(crew.getTitle(), response.getContent().get(0).getTitle());
            assertEquals(crew.getContent(), response.getContent().get(0).getContent());
            assertEquals(crew.getStrict(), response.getContent().get(0).getStrict());
            assertEquals(crew.getCrewLimit(), response.getContent().get(0).getCrewLimit());
        }

        @Test
        @DisplayName("크루 게시글 전체조회 실패1 : DB 에러")
        void findAllCrews2() {

            //given
            given(crewRepository.findAll((Pageable) any())).willThrow(new AppException(ErrorCode.DB_ERROR, ErrorCode.DB_ERROR.getMessage()));

            //when
            AppException exception = assertThrows(AppException.class, () -> crewService.findAllCrews(any()));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.DB_ERROR);
            assertEquals(exception.getMessage(), ErrorCode.DB_ERROR.getMessage());
        }
    }
}