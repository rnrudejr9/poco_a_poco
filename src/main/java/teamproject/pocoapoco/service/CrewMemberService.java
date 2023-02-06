package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberDeleteResponse;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.CrewMembers;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewMemberRepository;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewMemberService {
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    // 현재 모임에 참여하고 잇는 memberlist 출력
    @Transactional(readOnly = true)
    public List<CrewMemberResponse> getJoinMemberList(Long crewId) {
        List<CrewMembers> members = crewMemberRepository.findMembersByCrewId(crewId);
        return CrewMembers.from(members);
    }
    // 모임 참여 기능
    public CrewMemberResponse joinCrew(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        CrewMemberResponse joinMemberResponse = new CrewMemberResponse();
        if(user.getMembers().stream().anyMatch(members -> members.getCrew().equals(crew))){ // 이미 참여한 모임
            joinMemberResponse.setJoinCheck(0); // joinCheck 0으로 설정하고 db에 추가하지 x

        } else {
            crewMemberRepository.save(CrewMembers.builder().crew(crew).user(user).build()); // db에 추가 o
            joinMemberResponse.setJoinCheck(1); // joinCheck 1로 설정

        } // 여기 조건 추가한다면 참여가능한 모임을 조정할 수 있을 것
        return joinMemberResponse;
    }
    // 모임 나가기 기능
    @Transactional
    public CrewMemberDeleteResponse leaveCrew(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        crewMemberRepository.deleteByUserAndCrew(user, crew);
        return CrewMemberDeleteResponse.of(user.getId());
    }
}
