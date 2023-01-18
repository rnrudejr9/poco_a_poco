package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.repository.LikeRepository;
import teamproject.pocoapoco.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    public LikeResponse goodCrew(Long crewId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new AppException());
        Crew crew = crewRepository.findById(crewId).orElseThrow(()->new AppException());
        LikeResponse goodResponse = new LikeResponse();
        if(user.getLikes().stream().anyMatch(like -> like.equals(crew))){
            likeRepository.deleteByUserAndCrew(user,crew);
            goodResponse.setMessage("좋아요 취소");
        } else {
            likeRepository.save(Like.builder().crew(crew).user(user).build());
            goodResponse.setMessage("좋아요 성공");
        }
        return goodResponse;
    }
}
