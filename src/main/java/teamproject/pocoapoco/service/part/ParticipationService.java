package teamproject.pocoapoco.service.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.error.ErrorResponse;
import teamproject.pocoapoco.domain.dto.part.PartDto;
import teamproject.pocoapoco.domain.dto.part.PartJoinDto;
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


    //참여 기능
    @Transactional
    public Response participate(PartJoinDto partJoinDto){
        Crew crew = crewRepository.findById(partJoinDto.getCrewId()).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(partJoinDto.getUserName()).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));

        int size = 0;
        for(Participation participation : crew.getParticipations()){
            if(participation.getStatus()==2){
                size++;
            }
        }

        //같거나 클 경우에는 참여 못함
        if(size >= crew.getCrewLimit()){
            return Response.error(new ErrorResponse(ErrorCode.NOT_ALLOWED_PARTICIPATION,ErrorCode.NOT_ALLOWED_PARTICIPATION.getMessage()));
        }

        Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
        participation.setStatus(2);
        return Response.success("참여하기 성공");
    }

    @Transactional
    public Response reject(PartJoinDto partJoinDto){
        Crew crew = crewRepository.findById(partJoinDto.getCrewId()).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(partJoinDto.getUserName()).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));

        Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
        participation.setStatus(0);
        return Response.success("신청이 취소됨");
    }

    @Transactional
    public Response generatePart(PartDto partDto, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(partDto.getCrewId()).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));

        if(participationRepository.existsByCrewAndAndUser(crew,user)){
            Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
            if(participation.getStatus() == 1){
                participation.setStatus(0);
                return Response.success("참여하기 취소");
            }
            if(participation.getStatus() == 0){
                participation.setStatus(1);
                return Response.success("참여 신청완료");
            }
        }
        Participation savedParticipation = Participation.builder().crew(crew).title(crew.getTitle()).body(partDto.getBody()).user(user).status(1).build();
        for(Crew c : user.getCrews()) {
            if(c.equals(crew)) {
                savedParticipation.setStatus(2);
            }
        }
        participationRepository.save(savedParticipation);
        return Response.success("참여하기 동작");
    }

    //참여유무확인
    @Transactional
    public PartResponse findParticipate(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        if(!participationRepository.existsByCrewAndAndUser(crew,user)){
            return PartResponse.builder().status(0).build();
        }
        Participation participation = participationRepository.findByCrewAndUser(crew,user).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,ErrorCode.DB_ERROR.getMessage()));
        return PartResponse.builder().now(crew.getParticipations().size()).limit(crew.getCrewLimit()).status(participation.getStatus()).build();
   }


   //현재 크루 참여자 수 확인
   @Transactional
   public PartResponse findCrewInfo(Long crewId){
       Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
       int size = 0;
       for(Participation p : crew.getParticipations()){
           if(p.getStatus() == 2){
               size++;
           }
       }
       return PartResponse.builder().now(size).build();
   }


   //미승인된 멤버 조회
   @Transactional
   public List<PartJoinResponse> notAllowedMember(String userName){
       User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
       List<PartJoinResponse> participations = new ArrayList<>();
       for(Crew crew : user.getCrews()) {
           for (Participation participation : crew.getParticipations()) {
               if (participation.getStatus() == 1) {
                   participations.add(PartJoinResponse.builder()
                           .crewId(participation.getCrew().getId())
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

   //승인된 멤버 조회
   @Transactional
   public List<PartJoinResponse> AllowedMember(long crewId){
       Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
       List<PartJoinResponse> list = new ArrayList<>();
       for(Participation p : crew.getParticipations()){
           if(p.getStatus() == 2){
               PartJoinResponse partJoinResponse =  PartJoinResponse.builder()
                       .crewTitle(crew.getTitle())
                       .status(p.getStatus())
                       .writerUserName(crew.getUser().getNickName())
                       .joinUserName(p.getUser().getNickName())
                       .now(crew.getParticipations().size())
                       .limit(crew.getCrewLimit())
                       .build();
               list.add(partJoinResponse);
           }
       }
       return list;
   }


}
