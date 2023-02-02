package teamproject.pocoapoco.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.chat.Chat;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findByChatRoom(ChatRoom chatRoom);
}

