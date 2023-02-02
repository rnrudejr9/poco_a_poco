package teamproject.pocoapoco.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.chat.ChatRoomDTO;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.chat.ChatRoomRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomDTO> findAllRooms(){
        List<ChatRoom> result = chatRoomRepository.findAll();
        List<ChatRoomDTO> list =  ChatRoomDTO.createList(result);
        return list;
    }

    public ChatRoom findRoomById(Long id){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(id).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,""));
        return chatRoom;
    }

    public ChatRoom createChatRoomDTO(String name){
        ChatRoom savedRoom = chatRoomRepository.save(ChatRoom.builder().name(name).build());
        return savedRoom;
    }
}
