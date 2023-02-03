package teamproject.pocoapoco.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.chat.ChatMessageDTO;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.chat.Chat;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.chat.ChatRepository;
import teamproject.pocoapoco.repository.chat.ChatRoomRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    @Transactional
    public Response addChat(ChatMessageDTO chatMessageDTO){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId()).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,""));
        Chat chater = chatRepository.save(chatMessageDTO.toChat(chatRoom));
        return Response.success(chater);
    }

    public List<Chat> listChat(Long id){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(id).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,""));
        List<Chat> list = chatRepository.findByChatRoom(chatRoom);
        return list;
    }
}