package teamproject.pocoapoco.service.livematch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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
import java.util.*;

import static teamproject.pocoapoco.controller.main.api.sse.SseController.randomMatchListCnt;
import static teamproject.pocoapoco.controller.main.api.sse.SseController.sseEmitters;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveMatchService {
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final ParticipationRepository participationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String randomKey = "randomMatching";
    private HashMap<String, String> usersSports = new HashMap<>();


    @Transactional
    public int randomMatch(String username, String sport) {

        log.info("==================실시간 매칭 Service 로직에 들어왔습니다 - username = {}, sport = {}", username, sport);
        //처음에는 리스트로 하려고 했지만, 중복을 확인하는데 한계가 있어서 sorted set 사용

        // redis에 대기열 순서대로 삽입, 현재 시간을 score로 잡음
        redisTemplate.opsForZSet().add(sport, username, System.currentTimeMillis());
        // logout시에 sport를 확인하기 위해 저장
        usersSports.put(username + "_liveMatch", sport);

        // 현재 대기열의 총 숫자 확인
        Long randomMatchListInRedis = redisTemplate.opsForZSet().zCard(sport);
        log.info("현재 redis의 {} 종목 대기열의 숫자는 : {} 입니다",sport  ,randomMatchListInRedis);


        // 대기열에 있는 User들의 name을 꺼내온다. -> redis에서 삭제, user 찾을때 사용
        String[] UserListInRedis = new String[4];

//         대기리스트에 3명이 들어왔다면
        if (randomMatchListInRedis >= 3) {
            log.info("==========================실시간 매칭 대기열이 3명이상이여서 crew 생성 진입");

            // 대기열의 User들을 확인하기 위한 iterator
            Set<String> range = redisTemplate.opsForZSet().range(sport, 0, 3);

            Iterator<String> iterator = range.iterator();
            // iterator 돌면서 user string 으로 저장 -> user 찾기 위해
            int listCnt = 0;
            while (iterator.hasNext()) {
                String next = iterator.next();
                log.info("next = {}", next);
                UserListInRedis[listCnt] = next;
                log.info("redis의 대기열 = {}",UserListInRedis[listCnt]);
                listCnt++;
            }

            // User를 찾는다 -> 모임을 만들기 위해
            User fistUser = findUserFromRedis(UserListInRedis[0]);
            User secondUser = findUserFromRedis(UserListInRedis[1]);
            User thirdUser = findUserFromRedis(UserListInRedis[2]);
//            List<User> userList = findUserFromRedis1(UserListInRedis);


            Crew savedLiveMatchCrew = makeCrew(fistUser, secondUser, thirdUser, sport);
//            Crew savedLiveMatchCrew = makeCrew1(userList, sport);

            // participations 만들고 저장 후 crew에 저장
            makeParticipationsAndUpdateToCrew(fistUser, secondUser, thirdUser, savedLiveMatchCrew, sport);
//            makeParticipationsAndUpdateToCrew1(userList, savedLiveMatchCrew, sport);

            deleteRedisLiveMatchLists(UserListInRedis, sport);

            sendSseToUsers(fistUser, secondUser, thirdUser, savedLiveMatchCrew);
//            sendSseToUsers1(userList, savedLiveMatchCrew);

            /**
             * 변경
             */


            // 대기리스트 확인
            log.info("삭제된 후 redis 대기열 : {}",redisTemplate.opsForZSet().zCard(sport));
        }

        //sse에 대기인원 표시
        randomMatchListCnt = randomMatchListInRedis;
        if(randomMatchListCnt % 3 == 0) randomMatchListCnt = 0;

        return 1;
    }

    private void sendSse(User user, Crew savedLiveMatchCrew) {
        //sse 로직
        if (sseEmitters.containsKey(user.getUsername())) {
            log.info("실시간매칭 후 sse firstUser 작동");
            SseEmitter sseEmitter = sseEmitters.get(user.getUsername());
            try {
                sseEmitter.send(SseEmitter.event().name("liveMatch").data(
                        savedLiveMatchCrew.getChatRoom().getRoomId() + " " + savedLiveMatchCrew.getId()));
            } catch (Exception e) {
                sseEmitters.remove(savedLiveMatchCrew.getUser().getUsername());
            }
        }
    }
    private void sendSseToUsers(User fistUser, User secondUser, User thirdUser, Crew savedLiveMatchCrew) {
        //sse 로직
        //유저들을 String[]로 리펙토링해서 for문을 돌려보다
        //sendSse() 도 static으로 선언하면 조금 더 리펙토링 할 수 있지 않을까??
        sendSse(fistUser, savedLiveMatchCrew);
        sendSse(secondUser, savedLiveMatchCrew);
        sendSse(thirdUser, savedLiveMatchCrew);
    }



    private void deleteRedisLiveMatchLists(String[] UserListInRedis, String sport) {
        // 랜덤매칭이 이루어진 3명을 대기리스트에서 삭제
        redisTemplate.opsForZSet().remove(sport, UserListInRedis[0]);
        redisTemplate.opsForZSet().remove(sport, UserListInRedis[1]);
        redisTemplate.opsForZSet().remove(sport, UserListInRedis[2]);

//        // 실시간 매칭이 이루어진다면 redis에서 sport의 값을 확인하기 위한 set도 삭제해야한다.
//        // refresh token이 남아있다면 삭제
//        Set<String> keys = redisTemplate.keys("*"); //key를 하면 시간이 오래 소요됨
//        log.info("key가 몇개 존재하는지 확인: {}",keys.size());
//        for (String key:keys) {
//            log.info("key 정보 모두 조회: {}", key);
//        }

        //hashMap에서 user sports 삭제
        usersSports.remove(UserListInRedis[0] + "_liveMatch");
        usersSports.remove(UserListInRedis[1] + "_liveMatch");
        usersSports.remove(UserListInRedis[2] + "_liveMatch");

        if(redisTemplate.opsForValue().get(UserListInRedis[0] + "_liveMatch")!= null) {
            log.info("첫번째 redis set 삭제 = {}", UserListInRedis[0] + "_liveMatch");
            redisTemplate.delete(UserListInRedis[0] + "_liveMatch");
        }
        if(redisTemplate.opsForValue().get(UserListInRedis[1] + "_liveMatch")!= null) {
            log.info("두번쨰 redis set 삭제 = {}", UserListInRedis[1] + "_liveMatch");
            redisTemplate.delete(UserListInRedis[1] + "_liveMatch");
        }
        if(redisTemplate.opsForValue().get(UserListInRedis[2] + "_liveMatch")!= null) {
            log.info("세번째 redis set 삭제 = {}", UserListInRedis[2] + "_liveMatch");
            redisTemplate.delete(UserListInRedis[2] + "_liveMatch");
        }

//        Set<String> keysAfterRemove = redisTemplate.keys("*");
//        log.info("삭제 후 ------------- key가 몇개 존재하는지 확인: {}",keysAfterRemove.size());
//        for (String key:keysAfterRemove) {
//            log.info("삭제 후 ------------- key 정보 모두 조회: {}", key);
//        }
    }

    private void makeParticipationsAndUpdateToCrew(User fistUser, User secondUser, User thirdUser, Crew savedLiveMatchCrew, String sport) {
        // participations 만들기
        List<Participation> participationList = new ArrayList<>();

        Participation firstParticipation = Participation.builder()
                .status(2)
                .user(fistUser)
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
                .build();

        Participation secParticipation = Participation.builder()
                .status(2)
                .user(secondUser)
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
                .build();

        Participation thirdParticipation = Participation.builder()
                .status(2)
                .user(thirdUser)
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
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
        savedLiveMatchCrew.setParticipations(participationList);
    }



    private Crew makeCrew(User fistUser, User secondUser, User thirdUser, String sport) {
        // 방을 만들고 채팅방을 생성
        Crew crew = Crew.builder()
                .strict("청진동 246 D1동 16층, 17층 ")
                .roadName("서울 종로구 종로3길 17 D1동 16층, 17층")
                .title(sport + "실시간 매칭🔥")
                .content(fistUser.getUsername() + "님, " + secondUser.getUsername() + "님, "
                        + thirdUser.getUsername()  + "님\n"
                        + "실시간 매칭이 성사되었습니다 \n" +
                        "채팅방에서 시간 장소를 조율해주세요")
                .crewLimit(3)
                .datepick(LocalDateTime.now().toString())
                .timepick(LocalDateTime.now().toString())
                .chatRoom(ChatRoom.builder()
                        .name(sport + "실시간 매칭")
                        .user(fistUser)
                        .build()) //user에 참여자중 한명 넣으면 된다.. name = 타이틀이름
                .user(fistUser)  // crew 만든사람
                .build();
        return crewRepository.save(crew);
    }

    @Transactional
    public int randomMatchCancel(@RequestParam String username) {
        String sport = usersSports.get(username + "_liveMatch");
        log.info("sport = {}", sport);
        // redis에 있는 userName을 삭제
        redisTemplate.opsForZSet().remove(sport, username);
        log.info("logout user = {}", username);
//        redisTemplate.delete(username + "_liveMatch");
        usersSports.remove(username + "_liveMatch");

        Long randomMatchListInRedisCnt = redisTemplate.opsForZSet().zCard(sport);
        log.info("현재 redis의 대기열의 숫자는 : {} 입니다", randomMatchListInRedisCnt);

        //sse에 대기인원 표시
        randomMatchListCnt = randomMatchListInRedisCnt;
        return 1;
    }

    private User findUserFromRedis(String UserListInRedis) {
        return userRepository.findByUserName(UserListInRedis).orElseThrow(() -> {
            return new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        });
    }

    private void sendSseToUsers1(List<User> userList, Crew savedLiveMatchCrew) {
        //sse 로직
        //유저들을 String[]로 리펙토링해서 for문을 돌려보다
        //sendSse() 도 static으로 선언하면 조금 더 리펙토링 할 수 있지 않을까??
        for (int i = 0; i < userList.size(); i++) {
            sendSse(userList.get(i), savedLiveMatchCrew);
        }
    }

    private Crew makeCrew1(List<User> userList, String sport) {
        // 방을 만들고 채팅방을 생성
        Crew crew = Crew.builder()
                .strict("청진동 246 D1동 16층, 17층 ")
                .roadName("서울 종로구 종로3길 17 D1동 16층, 17층")
                .title(sport + "실시간 매칭🔥")
                .content(userList.get(0).getUsername() + "님, " + userList.get(1).getUsername() + "님, "
                        + userList.get(2).getUsername()  + "님\n"
                        + "실시간 매칭이 성사되었습니다 \n" +
                        "채팅방에서 시간 장소를 조율해주세요")
                .crewLimit(3)
                .datepick(LocalDateTime.now().toString())
                .timepick(LocalDateTime.now().toString())
                .chatRoom(ChatRoom.builder()
                        .name(sport + "실시간 매칭")
                        .user(userList.get(0))
                        .build()) //user에 참여자중 한명 넣으면 된다.. name = 타이틀이름
                .user(userList.get(0))  // crew 만든사람
                .build();
        return crewRepository.save(crew);
    }

    private List<User> findUserFromRedis1(String[] userListInRedis) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < userListInRedis.length; i++) {
            log.info("user make list = {}",userListInRedis[i]);
            userList.add(userRepository.findByUserName(userListInRedis[i]).orElseThrow(() -> {
                return new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
            }));
        }
        return userList;
    }

    private void makeParticipationsAndUpdateToCrew1(List<User> userList, Crew savedLiveMatchCrew, String sport) {
        // participations 만들기
        List<Participation> participationList = new ArrayList<>();

        Participation firstParticipation = Participation.builder()
                .status(2)
                .user(userList.get(0))
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
                .build();

        Participation secParticipation = Participation.builder()
                .status(2)
                .user(userList.get(1))
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
                .build();

        Participation thirdParticipation = Participation.builder()
                .status(2)
                .user(userList.get(2))
                .crew(savedLiveMatchCrew)
                .title(sport + "실시간 매칭🔥")
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
        savedLiveMatchCrew.setParticipations(participationList);
    }
}
