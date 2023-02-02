package teamproject.pocoapoco.domain.dto.chat;

import lombok.Getter;
import lombok.Setter;
import teamproject.pocoapoco.domain.entity.chat.Chat;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDTO {

    private Long roomId;
    private String writer;
    private String message;

    private LocalDateTime createdAt;
    public Chat toChat(ChatRoom chatRoom){
        return Chat.builder().message(message)
                .writer(writer)
                .chatRoom(chatRoom)
                .isChecked(false)
                .build();
    }
}