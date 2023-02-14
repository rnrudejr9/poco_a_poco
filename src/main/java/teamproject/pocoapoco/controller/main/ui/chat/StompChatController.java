package teamproject.pocoapoco.controller.main.ui.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import teamproject.pocoapoco.domain.dto.chat.ChatMessageDTO;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    private final Map<String, Long> map = new HashMap<>();

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO message){
        List<String> liveUser = new ArrayList<>();
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");

        map.put(message.getWriter(),message.getRoomId());
        for(Map.Entry<String, Long> entry : map.entrySet()){
            if(entry.getValue().equals(message.getRoomId()) ){
                liveUser.add(entry.getKey());
            }
        }
        message.setUserList(liveUser);
        message.setState(0);
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/out")
    public void out(ChatMessageDTO message){
        message.setMessage(message.getWriter() + "님이 채팅방에 나가셨습니다.");

        List<String> liveUser = new ArrayList<>();
        map.remove(message.getWriter());
        for(Map.Entry<String, Long> entry : map.entrySet()){
            if(entry.getValue().equals(message.getRoomId()) ){
                liveUser.add(entry.getKey());
            }
        }
        message.setUserList(liveUser);
        message.setState(1);
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO message){
        List<String> liveUser = new ArrayList<>();
        for(Map.Entry<String, Long> entry : map.entrySet()){
            if(entry.getValue().equals(message.getRoomId()) ){
                liveUser.add(entry.getKey());
            }
        }
        message.setUserList(liveUser);
        message.setState(0);
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
