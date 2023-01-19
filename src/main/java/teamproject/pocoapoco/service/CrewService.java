package teamproject.pocoapoco.service;


import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.crew.CrewAddRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewAddResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;


@Service
// DTO => Entity
// Entity => DTO
@RequiredArgsConstructor
@ToString
@Slf4j
public class CrewService {

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;


    // 크루 게시글 등록
    public CrewAddResponse addCrew(CrewAddRequest crewAddRequest, String userName) {

        // User 존재 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

        // DB 저장
        Crew crew = crewAddRequest.toEntity(user);
        crewRepository.save(crew);

        return new CrewAddResponse("Crew 등록 완료", crew.getId());
    }


    // 크루 게시글 수정
    public CrewAddResponse updateCrew(Long crewId, CrewAddRequest crewAddRequest, String userName) {

        // User 존재 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

        // 크루 게시글 존재 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

        // 해당 게시글 작성자 확인, 권한 확인
        if (!user.getCrews().contains(crew) && user.getRole().equals(UserRole.ROLE_USER)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "해당 게시글에 접근 권한이 없습니다.");
        }

        // DB 저장
        crewRepository.save(crewAddRequest.toEntity(user));

        return new CrewAddResponse("Crew 수정 완료", 1L);
    }

    // 크루 게시글 삭제
    public CrewAddResponse deleteCrew(Long crewId, String userName) {

        // User 존재 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));

        // 크루 게시글 존재 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));

        // 해당 게시글 작성자 확인, 권한 확인
        if (!user.getCrews().contains(crew) && user.getRole().equals(UserRole.ROLE_USER)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "해당 게시글에 접근 권한이 없습니다.");
        }

        // 크루 게시글 삭제
        crewRepository.delete(crew);

        return new CrewAddResponse("Crew 삭제 완료", crewId);
    }
}
