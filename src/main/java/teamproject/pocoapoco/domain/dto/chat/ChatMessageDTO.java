package teamproject.pocoapoco.domain.dto.chat;

import lombok.*;
import teamproject.pocoapoco.domain.entity.chat.Chat;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private Long roomId;
    private String writer;
    private String message;

    private String createdAt;
    public Chat toChat(ChatRoom chatRoom){
        return Chat.builder().message(message)
                .writer(writer)
                .chatRoom(chatRoom)
                .isChecked(false)
                .build();
    }

}