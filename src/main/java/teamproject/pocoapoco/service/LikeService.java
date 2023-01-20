package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.LikeRepository;
import teamproject.pocoapoco.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    public LikeResponse goodCrew(Long crewId, String userName){
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
        return goodResponse;
    }
}
