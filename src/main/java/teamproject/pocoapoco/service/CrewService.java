package teamproject.pocoapoco.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewSportRequest;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CrewService {

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;


    // 크루 게시글 등록
    public CrewResponse addCrew(CrewRequest crewRequest, String userName) {

        log.info("imagePath: {} ", crewRequest.getImagePath());
        log.info("datePick: {}", crewRequest.getDatepick());

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
    @Transactional
    public CrewResponse deleteCrew(Long crewId, String userName) {

        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);
        findByUserAndCrewContaining(user, crew);

        crew.deleteSoftly(LocalDateTime.now());
        crewRepository.save(crew);

        return new CrewResponse("Crew 삭제 완료", crewId);
    }

    @Transactional
    public CrewResponse finishCrew(Long crewId,String userName){
        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);
        findByUserAndCrewContaining(user,crew);

        crew.setFinish(1);
        return new CrewResponse("Crew 상태변경 완료",crewId);
    }





    // 크루 게시물 상세 조회
    public CrewDetailResponse detailCrew(Long crewId) {

//        User user = findByUserName(userName);
        Crew crew = findByCrewId(crewId);

        return CrewDetailResponse.of(crew);
    }

    // 크루 게시물 전체조회, 지역조회, 운동종목 조회
    @Transactional
    public Page<CrewDetailResponse> findAllCrewsByStrictAndSportEnum(CrewSportRequest crewSportRequest, boolean sportsListIsEmpty, Pageable pageable) {

        if (crewSportRequest.getStrict() == null && CollectionUtils.isEmpty(crewSportRequest.getSportsList()) && sportsListIsEmpty) {
            log.info("service findAllCrews : action");
            return findAllCrews(pageable);
        } else if (crewSportRequest.getStrict() != null && crewSportRequest.getStrict() != "") {
            log.info("service findAllCrewsByStrict : action");
            return findAllCrewsByStrict(crewSportRequest, pageable);
        } else {
            log.info("service findAllCrewsBySport : action");
            return findAllCrewsBySport(crewSportRequest.getSportsList(), pageable);
        }

    }


    // 크루 게시물 전체 조회
    @Transactional
    public Page<CrewDetailResponse> findAllCrews(Pageable pageable) {

        Page<Crew> crews = crewRepository.findAll(pageable);

        return crews.map(CrewDetailResponse::of);
    }

    // 크루 게시물 조회 By 지역 검색어
    @Transactional
    public Page<CrewDetailResponse> findAllCrewsByStrict(CrewSportRequest crewSportRequest, Pageable pageable) {

        Page<Crew> crews = crewRepository.findByStrictContaining(pageable, crewSportRequest.getStrict());

        return crews.map(CrewDetailResponse::of);
    }


    // 크루 게시물 조회 By 운동종목
    @Transactional
    public Page<CrewDetailResponse> findAllCrewsBySport(List<String> sportsList, Pageable pageable) {

        Page<Crew> crews;

        if (CollectionUtils.isEmpty(sportsList)) {
            crews = crewRepository.findAll(pageable);
        } else {
            SportEnum[] sports = new SportEnum[3];

            for (int i = 0; i < sportsList.size(); i++) {
                sports[i] = SportEnum.valueOf(sportsList.get(i)); // null
                log.info("Service sports List : {}", sports[i]);
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
    public Crew findByCrewId(Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage()));
    }

    // 해당 게시글 작성자 확인
    @Transactional
    public void findByUserAndCrewContaining(User user, Crew crew) {

        if(!user.getRole().equals(UserRole.ROLE_ADMIN)){
            if (!user.getCrews().contains(crew)) {
                throw new AppException(ErrorCode.INVALID_PERMISSION, "해당 게시글에 접근 권한이 없습니다.");
            }

        }

    }

    // 유저 선호 운동종목 확인
    @Transactional
    public List<String> getUserSports(Authentication authentication, Boolean sportsListIsEmpty) {

        List<String> userSportsList = new ArrayList<>();

        if (authentication != null && sportsListIsEmpty) {

            User user = findByUserName(authentication.getName());

            if(user.getSport().getSport1()!=null){
                userSportsList.add(String.valueOf(user.getSport().getSport1()));
            }
            if(user.getSport().getSport2()!=null){
                userSportsList.add(String.valueOf(user.getSport().getSport2()));
            }
            if(user.getSport().getSport3()!=null){
                userSportsList.add(String.valueOf(user.getSport().getSport3()));
            }


        }
        return userSportsList;
    }

    @Transactional
    public void readAlarms(Long crewId, String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
        List<Alarm> alarms = user.getAlarms();
        for (Alarm alarm : alarms) {
            boolean readOrNot = alarm.getReadOrNot();
            if (alarm.getTargetId() == crewId && !readOrNot) {
                alarm.setReadOrNot();
                log.info("알람을 읽었습니다 : {}        알림 : {}", alarm.getId(), alarm.getReadOrNot());
            }
        }
    }
    @Transactional
    public void readAlarmsReview(Long reviewId, String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage()));
        List<Alarm> alarms = user.getAlarms();
        for (Alarm alarm : alarms) {
            boolean readOrNot = alarm.getReadOrNot();
            if (alarm.getTargetId() == reviewId && !readOrNot) {
                alarm.setReadOrNot();
                log.info("알람을 읽었습니다 : {}        알림 : {}", alarm.getId(), alarm.getReadOrNot());
            }
        }
    }



    // 내가 참여한 crew list
    public Page<CrewDetailResponse> findAllCrew(Integer status, String userName,Pageable pageable) {
        User user = userRepository.findByUserName(userName).orElse(null);
        List<Participation> participations = participationRepository.findByStatusAndUser(status, user);
        Page<Crew> crewList = crewRepository.findByParticipationsIn(participations, pageable);
        return crewList.map(CrewDetailResponse::of);
    }
    // 내가 참여한 crew list
    public long getCrewByUserAndStatus(Integer status,String userName) {
        User user = userRepository.findByUserName(userName).orElse(null);
        List<Participation> participations = participationRepository.findByStatusAndUser(status, user);
        return crewRepository.countByParticipationsIn(participations);
    }

    // 강퇴하면 Participation을 지우는 방법 이용
    @Transactional
    public void deleteUserAtCrew(Long userId, Long crewId, String authenticationName){

        Optional<User> actingUserOptional = userRepository.findByUserName(authenticationName);

        Optional<Crew> crewOptional = crewRepository.findById(crewId);

        Optional<User> userOptional = userRepository.findById(userId);

        if(actingUserOptional.isEmpty()){
            throw new AppException(ErrorCode.FORBIDDEN_REQUEST, ErrorCode.FORBIDDEN_REQUEST.getMessage());
        }

        if(crewOptional.isEmpty()){
            throw new AppException(ErrorCode.CREW_NOT_FOUND, ErrorCode.CREW_NOT_FOUND.getMessage());
        }

        if(userOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User user = userOptional.get();

        User actingUser = actingUserOptional.get();

        Crew crew = crewOptional.get();

        Optional<Participation> participationOptional = participationRepository.findByCrewAndUser(crew, user);

        if(participationOptional.isEmpty()){
            throw new AppException(ErrorCode.NOT_FOUND_PARTICIPATION, ErrorCode.NOT_FOUND_PARTICIPATION.getMessage());
        }

        Participation befParticipation = participationOptional.get();

        // 방장 강퇴 못하도록 막음
        if(befParticipation.getUser().getId().equals(actingUser.getId())){
            throw new AppException(ErrorCode.NOT_AUTHORIZED, "방장은 강퇴할 수 없습니다.");
        } else{
            participationRepository.delete(befParticipation);
        }


        // 방장이거나 운영자인 경우만 삭제할 수 있는 기능 주석처리
//        if((actingUser.getRole() == UserRole.ROLE_ADMIN) || (crew.getUser().getId().equals(actingUser.getId()))){
//            Optional<Participation> participationOptional = participationRepository.findByCrewAndUser(crew, user);
//
//            if(participationOptional.isEmpty()){
//                throw new AppException(ErrorCode.NOT_FOUND_PARTICIPATION, ErrorCode.NOT_FOUND_PARTICIPATION.getMessage());
//            }
//
//            Participation befParticipation = participationOptional.get();
//
//            if(befParticipation.getUser().getId().equals(actingUser.getId())){
//                throw new AppException(ErrorCode.NOT_AUTHORIZED, "방장은 강퇴할 수 없습니다.");
//            } else{
//                participationRepository.delete(befParticipation);
//            }
//
//
//        }else{
//            throw new AppException(ErrorCode.NOT_AUTHORIZED, ErrorCode.NOT_AUTHORIZED.getMessage());
//        }

    }


}
