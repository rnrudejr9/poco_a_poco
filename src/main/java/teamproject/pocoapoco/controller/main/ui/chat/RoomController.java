package teamproject.pocoapoco.controller.main.ui.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import teamproject.pocoapoco.service.chat.ChatRoomService;
import teamproject.pocoapoco.service.chat.ChatService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/view/v1")
@Log4j2
public class RoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public String rooms(Model model){
        log.info("# All Chat Rooms");
        model.addAttribute("list",chatRoomService.findAllRooms());
        return "chat/rooms";
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public String create(@RequestParam String name, RedirectAttributes rttr){
        log.info("# Create Chat Room , name: " + name);
        rttr.addFlashAttribute("roomName", chatRoomService.createChatRoomDTO(name));
        return "redirect:/view/v1/rooms";
    }

    //채팅방 조회
    @GetMapping("/room")
    public String getRoom(Long roomId, Model model){
        log.info("# get Chat Room, roomID : " + roomId);
        model.addAttribute("room", chatRoomService.findRoomById(roomId));
        return "chat/room";
    }

}
