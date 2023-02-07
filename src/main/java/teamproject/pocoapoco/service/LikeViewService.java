package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamproject.pocoapoco.domain.dto.like.LikeViewResponse;
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

import java.util.List;

import static teamproject.pocoapoco.controller.main.api.sse.SseController.sseEmitters;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeViewService {
    private final LikeRepository likeRepository;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    // view 페이지 like 기능
    public int getLikeCrew(Long crewId){
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        List<Like> num = likeRepository.findByCrew(crew);
        return num.size();
    }
    @Transactional
    public LikeViewResponse pressLike(Long crewId, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        LikeViewResponse likeViewResponse = new LikeViewResponse();
        if(user.getLikes().stream().anyMatch(like -> like.getCrew().equals(crew))){
            likeRepository.deleteByUserAndCrew(user,crew);
            likeViewResponse.setLikeCheck(0);

        } else {
            likeRepository.save(Like.builder().crew(crew).user(user).build());
            alarmRepository.save(Alarm.toEntity(user, crew, AlarmType.LIKE_CREW, AlarmType.LIKE_CREW.getText()));
            likeViewResponse.setLikeCheck(1);

            //sse 로직
            if (sseEmitters.containsKey(crew.getUser().getUsername())) {
                log.info("userName이 Map으로 등록되어있어 알림 sse 작동됩니다.");
                log.info("Sse username = {}", crew.getUser().getUsername());
                SseEmitter sseEmitter = sseEmitters.get(crew.getUser().getUsername());
                try {
                    sseEmitter.send(SseEmitter.event().name("addComment").data(
                            userName + "님이 \"" + crew.getTitle() + "\" 모임에 좋아요를 눌렀습니다."));
                } catch (Exception e) {
                    sseEmitters.remove(crew.getUser().getUsername());
                }
            }
        }

        List<Like> num = likeRepository.findByCrew(crew);

        likeViewResponse.setCount(num.size());
        likeViewResponse.setUserName(user.getUsername());
        return likeViewResponse;
    }
}