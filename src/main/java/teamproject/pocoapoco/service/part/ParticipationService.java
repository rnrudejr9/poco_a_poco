package teamproject.pocoapoco.service.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.error.ErrorResponse;
import teamproject.pocoapoco.domain.dto.part.PartDto;
import teamproject.pocoapoco.domain.dto.part.PartJoinResponse;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public Response participate(Long crewId, String nickName){
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        User user = userRepository.findByNickName(nickName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));

        //같거나 클 경우에는 참여 못함
        if(crew.getParticipations().size() >= crew.getCrewLimit()){
            return Response.error(new ErrorResponse(ErrorCode.NOT_ALLOWED_PARTICIPATION,ErrorCode.NOT_ALLOWED_PARTICIPATION.getMessage()));
        }

        Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
        participation.setStatus(2);
        return Response.success("참여하기 성공");
    }

    @Transactional
    public Response generatePart(PartDto partDto, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(partDto.getCrewId()).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));

        if(participationRepository.existsByCrewAndAndUser(crew,user)){
            Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
            //존재하는 경우
            participationRepository.delete(participation);
            return Response.success("참여하기 취소");
        }else{
            //존재하지 않는 경우
            Participation savedParticipation = Participation.builder().crew(crew).title(crew.getTitle()).body(partDto.getBody()).user(user).status(1).build();

            //작성자일 경우 자동참여
            for(Crew c : user.getCrews()) {
                if(c.equals(crew)) {
                    savedParticipation.setStatus(2);
                }
            }
            participationRepository.save(savedParticipation);
            return Response.success("참여하기 승인대기");
        }
    }

    //참여유무확인
    public PartResponse findParticipate(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        if(!participationRepository.existsByCrewAndAndUser(crew,user)){
            return PartResponse.builder().status(0).build();
        }
        Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
        return PartResponse.builder().now(crew.getParticipations().size()).limit(crew.getCrewLimit()).status(participation.getStatus()).build();
   }

   public List<PartJoinResponse> notAllowedPart(String userName){
       User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
       List<PartJoinResponse> participations = new ArrayList<>();
       for(Crew crew : user.getCrews()) {
           for (Participation participation : crew.getParticipations()) {
               if (participation.getStatus() == 1) {
                   participations.add(PartJoinResponse.builder()
                           .body(participation.getBody())
                           .title(participation.getTitle())
                           .joinUserName(participation.getUser().getUsername())
                           .writerUserName(participation.getCrew().getUser().getUsername())
                           .crewTitle(participation.getCrew().getTitle())
                           .status(participation.getStatus()).build());
               }
           }
       }
       return participations;
   }


}
