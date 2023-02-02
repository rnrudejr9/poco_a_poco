package teamproject.pocoapoco.domain.entity.chat;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String name;
//    @Builder.Default
//    @OneToMany(mappedBy = "chat_room")
//    private List<Chat> chats = new ArrayList<>();

}
