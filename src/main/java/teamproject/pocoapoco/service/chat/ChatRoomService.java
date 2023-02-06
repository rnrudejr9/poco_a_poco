package teamproject.pocoapoco.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.chat.ChatRoomDTO;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.chat.ChatRoomRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final CrewRepository crewRepository;
    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;
    public List<ChatRoomDTO> findAllRooms(){
        List<ChatRoom> result = chatRoomRepository.findAll();
        List<ChatRoomDTO> list =  ChatRoomDTO.createList(result);
        return list;
    }

    public List<ChatRoom> findBy(String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        List<ChatRoom> list = user.getChatRooms();
        return list;
    }


    public List<ChatRoom> findByParticipation(String userName){
        //참여 여부로 채팅방 조회
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        List<ChatRoom> chatRoomList = new ArrayList<>();
        List<Participation> participationList = participationRepository.findByUser(user);
        for(Participation p : participationList){
            chatRoomList.add(p.getCrew().getChatRoom());
        }
        return chatRoomList;
    }

    public ChatRoom findRoomById(Long id){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(id).orElseThrow(()->new AppException(ErrorCode.DB_ERROR,""));
        return chatRoom;
    }

    @Transactional
    public ChatRoom createChatRoomDTO(ChatRoomDTO chatRoomDTO, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
        Crew crew = crewRepository.findById(chatRoomDTO.getCrewId()).orElseThrow(()->new AppException(ErrorCode.CREW_NOT_FOUND,ErrorCode.CREW_NOT_FOUND.getMessage()));
        ChatRoom chatRoom = ChatRoom.builder().name(chatRoomDTO.getName()).build();
        crew.setChatRoom(chatRoom);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return savedRoom;
    }
}
