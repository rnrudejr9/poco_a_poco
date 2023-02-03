package teamproject.pocoapoco.controller.main.api.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.pocoapoco.domain.dto.chat.ChatRoomDTO;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.chat.ChatRoomService;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
public class ChatRoomRestController {
    private final ChatRoomService chatRoomService;
    @PostMapping()
    public Response addRoom(@RequestBody ChatRoomDTO chatRoomDTO, Authentication authentication){
        chatRoomService.createChatRoomDTO(chatRoomDTO,authentication.getName());
        return Response.success("개설성공");
    }
}
