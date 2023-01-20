package teamproject.pocoapoco.service;


import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewStrictRequest;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


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
    public CrewResponse addCrew(CrewRequest crewRequest, String userName) {

        User user = getUserFindBy(userName);

        Crew crew = crewRequest.toEntity(user);

        crewRepository.save(crew);

        return new CrewResponse("Crew 등록 완료", crew.getId());
    }

    // 크루 게시글 수정
    public CrewResponse updateCrew(Long crewId, CrewRequest crewRequest, String userName) {

        User user = getUserFindBy(userName);

        Crew crew = getCrewFindBy(crewId);

        checkUser(user, crew);

        crew.update(crewRequest);
        crewRepository.save(crew);

        return new CrewResponse("Crew 수정 완료", crewId);
    }

    // 크루 게시글 삭제
    public CrewResponse deleteCrew(Long crewId, String userName) {

        User user = getUserFindBy(userName);

        Crew crew = getCrewFindBy(crewId);

        checkUser(user, crew);

        crewRepository.delete(crew);

        return new CrewResponse("Crew 삭제 완료", crewId);
    }

    // 크루 게시물 상세 조회
    public CrewDetailResponse detailCrew(Long crewId, String userName) {

        User user = getUserFindBy(userName);

        Crew crew = getCrewFindBy(crewId);

        return CrewDetailResponse.of(crew);
    }

    // 크루 게시물 전체 조회
    public List<CrewDetailResponse> allCrew(Pageable pageable) {

        Page<Crew> crews = crewRepository.findAll(pageable);

        List<CrewDetailResponse> responsesList = crews
                .stream()
                .map(crew -> CrewDetailResponse.of(crew))
                .collect(Collectors.toList());

        return responsesList;
    }

    // 크루 게시물 지역 검색 조회
    public List<CrewDetailResponse> allCrewWithSport(CrewStrictRequest crewStrictRequest, Pageable pageable) {

        Page<Crew> crews = crewRepository.findByStrictContaining(pageable, crewStrictRequest.getStrict());

        List<CrewDetailResponse> responsesList = crews
                .stream()
                .map(crew -> CrewDetailResponse.of(crew))
                .collect(Collectors.toList());

        return responsesList;
    }

    // User 존재 확인
    private User getUserFindBy(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
    }

    // 크루 게시글 존재 확인
    private Crew getCrewFindBy(Long crewId ) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
    }

    // 해당 게시글 작성자 확인
    private void checkUser(User user, Crew crew) {
        if (!user.getCrews().contains(crew)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "해당 게시글에 접근 권한이 없습니다.");
        }
    }

}
