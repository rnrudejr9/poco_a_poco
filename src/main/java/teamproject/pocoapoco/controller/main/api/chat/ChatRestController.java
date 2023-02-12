package teamproject.pocoapoco.controller.main.api.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.chat.ChatMessageDTO;
import teamproject.pocoapoco.domain.dto.check.CheckRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.chat.ChatService;
import teamproject.pocoapoco.service.chat.ChatConfigService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@RestController
@Slf4j
public class ChatRestController {
    private final ChatService chatService;
    private final ChatConfigService checkService;

    @PostMapping()
    public void addChat(@RequestBody ChatMessageDTO chatMessageDTO){
        log.info(chatMessageDTO.getMessage()+" "+chatMessageDTO.getWriter());
        chatService.addChat(chatMessageDTO);
    }

    @GetMapping("/{roomId}")
    public Response listChat(@PathVariable Long roomId){
        log.info("roomId : " + roomId);
        List<ChatMessageDTO> list = chatService.listChat(roomId);
        return Response.success(list);
    }

    @PostMapping("/check")
    public Response checkSave(@RequestBody CheckRequest checkRequest){
        return checkService.checkSave(checkRequest);
    }

    @GetMapping("/check/{roomId}/{userName}")
    public Response checkRead(@PathVariable Long roomId, @PathVariable String userName){
        return checkService.checkRead(roomId,userName);
    }
}
