package teamproject.pocoapoco.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CrewService {

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    // 크루 게시글 등록
    public CrewResponse addCrew(CrewRequest crewRequest, String userName) {

        User user = findByUserName(userName);

        Crew crew = crewRepository.save(crewRequest.toEntity(user));

        return new CrewResponse("Crew 등록 완료", crew.getId());
    }

    // 크루 게시글 수정
    public CrewResponse modifyCrew(Long crewId, CrewRequest crewRequest, String userName) {

        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);
        findByUserAndCrewContaining(user, crew);

        crew.of(crewRequest);
        crewRepository.save(crew);

        return new CrewResponse("Crew 수정 완료", crewId);
    }

    // 크루 게시글 삭제
    public CrewResponse deleteCrew(Long crewId, String userName) {

        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);
        findByUserAndCrewContaining(user, crew);

        crew.deleteSoftly(LocalDateTime.now());
        crewRepository.save(crew);

        return new CrewResponse("Crew 삭제 완료", crewId);
    }

    // 크루 게시물 상세 조회
    public CrewDetailResponse detailCrew(Long crewId) {

//        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);

        return CrewDetailResponse.of(crew);
    }

    // 크루 게시물 전체 조회
    @Transactional
    public Page<CrewDetailResponse> findAllCrews(Pageable pageable) {

        Page<Crew> crews = crewRepository.findAll(pageable);

        return crews.map(CrewDetailResponse::of);
    }

    // 크루 게시물 지역 검색 조회
    public Page<CrewDetailResponse> findAllCrewsWithStrict(CrewSportRequest crewSportRequest, Pageable pageable) {

        Page<Crew> crews = crewRepository.findByStrictContaining(pageable, crewSportRequest.getStrict());

        return crews.map(CrewDetailResponse::of);
    }


    // test : 크루 게시물 검색 조회
    public Page<CrewDetailResponse> findAllCrewsBySport(CrewSportRequest crewSportRequest, Pageable pageable) {

        //전체검색
//        Page<Crew> crews = crewRepository.findAll(pageable);

        //지역 검색 by String
//        String strict = "대구";
//        Page<Crew> crews = crewRepository.findByStrictContaining(pageable, strict);


        //운동 검색 by String
//        String sport = "축구";
//        Page<Crew> crews = crewRepository.findBySprotStrContaining(pageable, sport);

        //운동 다중검색1 by String
//        String sport = "축구";
//        String sport2 = "";
//        String sport3 = "";
//        Page<Crew> crews = crewRepository.findBySprotStrOrSprotStrOrSprotStr(pageable, sport, sport2, sport3);


        //운동 다중검색2 by String
//        String sport = "축구";
//        String sport2 = "";
//        String sport3 = "";
//
//        if(crewSportRequest.getSportsList().contains(SportEnum.SOCCER))
//            sport ="축구";
//        if(crewSportRequest.getSportsList().contains(SportEnum.SOCCER))
//            sport2="조깅";
//        if(crewSportRequest.getSportsList().contains(SportEnum.SOCCER))
//            sport3="테니스";
//
//        Page<Crew> crews = crewRepository.findBySprotStr(pageable, sport, sport2, sport3);


        //운동 검색 by Enum

        Page<Crew> crews;
        List<String> sportsList = crewSportRequest.getSportsList();

        if (sportsList.isEmpty()) {
            crews = crewRepository.findAll(pageable);
        } else {
            SportEnum[] sports = new SportEnum[3];

            for (int i = 0; i < sportsList.size(); i++) {
                sports[i] = SportEnum.valueOf(sportsList.get(i));
            }

            crews = crewRepository.findBySportEnum(pageable, sports[0], sports[1], sports[2]);
        }
        return crews.map(CrewDetailResponse::of);
    }


    // User 존재 확인
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
    }

    // 크루 게시글 존재 확인
    private Crew findByCrewId(Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
    }

    // 해당 게시글 작성자 확인
    private void findByUserAndCrewContaining(User user, Crew crew) {
        if (!user.getCrews().contains(crew)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "해당 게시글에 접근 권한이 없습니다.");
        }
    }

}
