package teamproject.pocoapoco.domain.entity.chat;

import lombok.*;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.check.ChatConfigEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "chatRoom")
    private List<ChatConfigEntity> chatConfigEntities = new ArrayList<>();
//    @Builder.Default
//    @OneToMany(mappedBy = "chat_room")
//    private List<Chat> chats = new ArrayList<>();

}
