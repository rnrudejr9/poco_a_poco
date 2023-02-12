package teamproject.pocoapoco.controller.main.api.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;
import teamproject.pocoapoco.service.livematch.LiveMatchService;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
@Slf4j
public class RandomMatchController {
    //랜던매치 누르면 작동
    private final LiveMatchService liveMatchService;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final ParticipationRepository participationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String randomComment = "랜덤매칭입니다. 채팅방에서 협의 후 결정해 주세요";
    private final String randomKey = "randomMatching";

    @PostMapping("/random")
    @Transactional
    public int randomMatch(@RequestParam String username, @RequestParam String sport) {
        return liveMatchService.randomMatch(username, sport);
    }

    @PostMapping("/random/cancel")
    @Transactional
    public int randomMatchCancel(@RequestParam String username) {
        return liveMatchService.randomMatchCancel(username);
    }

    private User findUserFromRedis(String matchListInRedis) {
        return userRepository.findByUserName(matchListInRedis).orElseThrow(() -> {
            return new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        });
    }
}
