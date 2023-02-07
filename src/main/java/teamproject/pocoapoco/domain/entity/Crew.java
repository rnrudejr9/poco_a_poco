package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import teamproject.pocoapoco.domain.dto.chat.ChatRoomDTO;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
public class Crew extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String strict;
    private String roadName;
    private String title;
    private String content;
    private Integer crewLimit;

    private String imagePath;

    private String datepick;
    private String timepick;



    @OneToOne
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom;

    //crew 종목 검색 test
    private String sprotStr;
    @Enumerated(value = EnumType.STRING)
    private InterestSport interestSport;

    @Enumerated(value = EnumType.STRING)
    private SportEnum sportEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "crew")
    private List<Like> likes = new ArrayList<>();

    @OneToOne
    private CrewMembers members;

    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    public void of(CrewRequest request) {
        this.strict = request.getStrict();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.crewLimit = request.getCrewLimit();
    }
}
