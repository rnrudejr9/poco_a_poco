package teamproject.pocoapoco.service.part;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public void participate(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        if(participationRepository.existsByCrewAndAndUser(crew,user)){
            Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
            //존재하는 경우
            participationRepository.delete(participation);
        }else{
            //존재하지 않는 경우
            participationRepository.save(Participation.builder().crew(crew).user(user).build());
        }
    }
}
