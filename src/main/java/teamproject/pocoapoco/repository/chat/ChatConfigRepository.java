package teamproject.pocoapoco.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.domain.entity.check.ChatConfigEntity;

import java.util.Optional;

public interface ChatConfigRepository extends JpaRepository<ChatConfigEntity, Long> {
    Optional<ChatConfigEntity> findByUserAndChatRoom(User user, ChatRoom chatRoom);
    Boolean existsByUserAndChatRoom(User user,ChatRoom chatRoom);
}
