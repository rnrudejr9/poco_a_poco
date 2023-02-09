package teamproject.pocoapoco.controller.main.api.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
@Slf4j
public class RandomMatchController {

    static int randomMatchCnt;
    static int matchOrder;

    private final RedisTemplate<String, String> redisTemplate;
    @PostMapping("/random")
    public int randomMatch(@RequestParam String username) {
        if(randomMatchCnt % 3 == 0) randomMatchCnt = 0;
        log.info("username = {}", username);
        redisTemplate.opsForZSet().add("randomMatching", username, ++matchOrder);
//        String isInit = redisTemplate.opsForZSet().;
//        log.info("redis에 저장되었는지 확인 = {}",isInit);
        return ++randomMatchCnt;
    }

}
