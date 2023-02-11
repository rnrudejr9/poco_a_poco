package teamproject.pocoapoco.service.livematch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static teamproject.pocoapoco.controller.main.api.sse.SseController.randomMatchListCnt;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveMatchService {
    //랜던매치 누르면 작동
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final ParticipationRepository participationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String randomComment = "랜덤매칭입니다. 채팅방에서 협의 후 결정해 주세요";
    private final String randomKey = "randomMatching";

    @Transactional
    public int randomMatch(@RequestParam String username, @RequestParam String sport) {

        log.info("RandomMatchController - username = {}, sport = {}", username, sport);

        //처음에는 리스트로 하려고 했지만, 중복을 확인하는데 한계가 있어서 sorted set 사용

        // redis에 대기열 순서대로 삽입, 현재 시간을 score로 잡음
        redisTemplate.opsForZSet().add(randomKey, username, System.currentTimeMillis());
        // 삭제를 위해 set에 sport 저장 -> 자바스크립트에서 sport 매개변수로 줄 수는 없을까?
        redisTemplate.opsForSet().add(username,sport);

        // 현재 대기열의 총 숫자 확인
        Long randomMatchListInRedis = redisTemplate.opsForZSet().zCard(randomKey);

        // 대기열의 User들을 확인하기 위한 iterator
        Set<String> range = redisTemplate.opsForZSet().range(randomKey, 0, 3);
        Iterator<String> iterator = range.iterator();

        // 대기열에 있는 User들의 name을 꺼내온다. -> redis에서 삭제, user 찾을때 사용
        String[] matchListInRedis = new String[4];

        log.info("현재 redis의 대기열의 숫자는 : {} 입니다", randomMatchListInRedis);

//         대기리스트에 3명이 들어왔다면
        if (randomMatchListInRedis >= 3) {
            log.info("랜덤매칭 대기열이 3명이상이여서 crew 생성 진입");

            // iterator 돌면서 user string 으로 저장 -> user 찾기 위해
            int listCnt = 0;
            while (iterator.hasNext()) {
                String next = iterator.next();
                log.info("next = {}", next);
                matchListInRedis[listCnt] = next;
                log.info("redis의 대기열 = {}",matchListInRedis[listCnt]);
                listCnt++;
            }

            // User를 찾는다 -> 모임을 만들기 위해
            User fistUser = findUserFromRedis(matchListInRedis[0]);
            User secondUser = findUserFromRedis(matchListInRedis[1]);
            User thirdUser = findUserFromRedis(matchListInRedis[2]);

            // 방을 만들고 채팅방을 생성
            Crew crew = Crew.builder()
                    .strict(randomComment)
                    .roadName(randomComment)
                    .title(randomComment)
                    .content(randomComment)
                    .crewLimit(3)
                    .datepick(LocalDateTime.now().toString())
                    .timepick(LocalDateTime.now().toString())
                    .chatRoom(ChatRoom.builder()
                            .name(randomComment)
                            .user(fistUser)
                            .build()) //user에 참여자중 한명 넣으면 된다.. name = 타이틀이름
                    .user(fistUser)  // crew 만든사람
//                    .participations() //참여자 정보 = crew ID가 있어야 한다.
                    .build();
            Crew saveRandomMatchCrew = crewRepository.save(crew);

            // participations 만들기
            List<Participation> participationList = new ArrayList<>();

            Participation firstParticipation = Participation.builder()
                    .status(2)
                    .user(fistUser)
                    .crew(saveRandomMatchCrew)
                    .title(randomComment)
                    .build();

            Participation secParticipation = Participation.builder()
                    .status(2)
                    .user(secondUser)
                    .crew(saveRandomMatchCrew)
                    .title(randomComment)
                    .build();

            Participation thirdParticipation = Participation.builder()
                    .status(2)
                    .user(thirdUser)
                    .crew(saveRandomMatchCrew)
                    .title(randomComment)
                    .build();

            // participation 저장
            participationRepository.save(firstParticipation);
            participationRepository.save(secParticipation);
            participationRepository.save(thirdParticipation);

            // list에 저장
            participationList.add(firstParticipation);
            participationList.add(secParticipation);
            participationList.add(thirdParticipation);



            //저장된 크루에 participations 저장
            saveRandomMatchCrew.setParticipations(participationList);


            // 랜덤매칭이 이루어진 3명을 대기리스트에서 삭제
            redisTemplate.opsForZSet().remove(randomKey, matchListInRedis[0]);
            redisTemplate.opsForZSet().remove(randomKey, matchListInRedis[1]);
            redisTemplate.opsForZSet().remove(randomKey, matchListInRedis[2]);

            // 대기리스트 확인
            log.info("삭제된 후 redis 대기열 : {}",redisTemplate.opsForZSet().zCard(randomKey));

        }

        //sse에 대기인원 표시
        randomMatchListCnt = randomMatchListInRedis;
        if(randomMatchListCnt % 3 == 0) randomMatchListCnt = 0;

        return 1;
    }

    @Transactional
    public int randomMatchCancel(@RequestParam String username) {
        // redis에 있는 userName을 삭제
        redisTemplate.opsForZSet().remove(randomKey, username);
//        redisTemplate.opsForSet().remove(username);

        Long randomMatchListInRedisCnt = redisTemplate.opsForZSet().zCard(randomKey);
        log.info("현재 redis의 대기열의 숫자는 : {} 입니다", randomMatchListInRedisCnt);

        //sse에 대기인원 표시
        randomMatchListCnt = randomMatchListInRedisCnt;
        return 1;
    }

    private User findUserFromRedis(String matchListInRedis) {
        return userRepository.findByUserName(matchListInRedis).orElseThrow(() -> {
            return new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        });
    }
}
