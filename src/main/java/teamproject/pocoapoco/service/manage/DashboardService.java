package teamproject.pocoapoco.service.manage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.enums.StrictEnum;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DashboardService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> userTrackingRedisTemplate;
//    private final RedisTemplate<String, String> redisTemplate;

    private final CrewRepository crewRepository;


    public Long getUserCount() {


//        ScanOptions options = ScanOptions.scanOptions().match("*").count(10).build(); // scan 을 이용하면 한 번에 10개씩
//        Cursor<byte[]> keys = redisConnection.scan(options);

        Set<String> keys = userTrackingRedisTemplate.keys("*"); //key를 하면 시간이 오래 소요됨
        log.info("key가 몇개 존재하는지 확인: {}",keys.size());
        for (String key:keys) {
            log.info("key 정보 모두 조회: {}", key);

        }


        return Long.valueOf(keys.size());
    }

    // 모임 수
    public Long getCrewCount(){
       return crewRepository.countBy();

    }

    //
    public Long getCrewCountByStrict(String strict){

        if(strict == null){
            return crewRepository.countBy();

        } else if (strict.isBlank()) {
            return crewRepository.countBy();

        } else{
            StrictEnum strictEnum = StrictEnum.valueOf(strict);
            return crewRepository.countByStrictContaining(strictEnum.getValue());
        }


    }



    // SportsString
    public Long getCrewCountBySportEnum(String sport){

        if(sport==null){
            return crewRepository.countBy();
        } else if (sport.isBlank()) {
            return crewRepository.countBy();
        } else{
            SportEnum sportEnum = SportEnum.valueOf(sport);
            return crewRepository.countBySportEnum(sportEnum);
        }

    }


}
