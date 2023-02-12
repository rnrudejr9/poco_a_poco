package teamproject.pocoapoco.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.domain.entity.part.Participation;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> findByRoomId(Long id);
}
