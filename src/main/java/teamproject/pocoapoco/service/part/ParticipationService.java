package teamproject.pocoapoco.service.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.error.ErrorResponse;
import teamproject.pocoapoco.domain.dto.part.PartResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public Response participate(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));

        int limitValue = crew.getCrewLimit();
        List<Participation> list = participationRepository.findByCrew(crew);
        if(list.size() >= limitValue){
            log.info("가득찼다");
            return Response.error(new ErrorResponse(ErrorCode.NOT_ALLOWED_PARTICIPATION,ErrorCode.NOT_ALLOWED_PARTICIPATION.getMessage()));
        }

        if(participationRepository.existsByCrewAndAndUser(crew,user)){
            Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
            //존재하는 경우
            participationRepository.delete(participation);
        }else{
            //존재하지 않는 경우
            participationRepository.save(Participation.builder().crew(crew).user(user).build());
        }

        return Response.success("참여하기/참여취소 기능 동작");
    }

    public PartResponse findParticipate(Long crewId){
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        return PartResponse.builder().now(crew.getParticipations().size()).limit(crew.getCrewLimit()).build();
    }
}
