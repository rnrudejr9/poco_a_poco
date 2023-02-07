package teamproject.pocoapoco.controller.main.api.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse")
@Slf4j
public class SseController {

    public static boolean alarmChecker;
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

//    @GetMapping
//    public SseEmitter streamSseMvc() {
//        SseEmitter emitter = new SseEmitter();
//        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
//        sseMvcExecutor.execute(() -> {
//            try {
//                while (true) {
//                    if (alarmChecker == true) {
//                        SseEmitter.SseEventBuilder event = SseEmitter.event()
//                                .data(System.currentTimeMillis());
//                        emitter.send(event);
//                        Thread.sleep(1000);
//                        alarmChecker = false;
//                    }
//                }
//            } catch (Exception ex) {
//                emitter.completeWithError(ex);
//            }
//        });
//        return emitter;
//    }
    @GetMapping(value = "", consumes = MediaType.ALL_VALUE)
    public SseEmitter streamSseMvc(@RequestParam String userId) {
        log.info("userId = {}", userId);

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            emitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        sseEmitters.put(userId, emitter);

        Iterator<String> iter = sseEmitters.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = String.valueOf(sseEmitters.get(key));

            log.info(key + " : " + value);
        }

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> sseEmitters.remove(userId));

        return emitter;
    }
}