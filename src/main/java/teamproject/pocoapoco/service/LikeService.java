package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.AlarmType;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.AlarmRepository;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.LikeRepository;
import teamproject.pocoapoco.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    public LikeResponse goodCrew(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        LikeResponse goodResponse = new LikeResponse();
        if(user.getLikes().stream().anyMatch(like -> like.getCrew().equals(crew))){
            likeRepository.deleteByUserAndCrew(user,crew);
            goodResponse.setMessage("좋아요 취소");
        } else {
            likeRepository.save(Like.builder().crew(crew).user(user).build());
            alarmRepository.save(Alarm.toEntity(user, crew, AlarmType.LIKE_CREW, AlarmType.LIKE_CREW.getText()));

            goodResponse.setMessage("좋아요 성공");
        }
        return goodResponse;
    }

    public LikeResponse getLike(Long crewId){
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        LikeResponse goodResponse = new LikeResponse();
        goodResponse.setMessage("메세지 개수는 총 : "+ crew.getLikes().size());
        return goodResponse;
    }
    public void updateLike(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        LikeResponse goodResponse = new LikeResponse();
        if(user.getLikes().stream().anyMatch(like -> like.getCrew().equals(crew))){
            likeRepository.deleteByUserAndCrew(user,crew);
            goodResponse.setMessage("좋아요 취소");
        } else {
            likeRepository.save(Like.builder().crew(crew).user(user).build());
            goodResponse.setMessage("좋아요 성공");
        }
    }

    public int selectLike(Long crewId){
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        LikeResponse goodResponse = new LikeResponse();
        int count = crew.getLikes().size();
        return count;
    }

}
