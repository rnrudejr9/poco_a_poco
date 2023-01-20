package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;
import teamproject.pocoapoco.enums.InterestSport;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private Integer chatroomId;
    // participant_id 추후 추가예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "crew")
    private List<Like> likes = new ArrayList<>();

    public void update(CrewRequest request) {
        this.strict = request.getStrict();
        this.title = request.getTitle();
        this.content =request.getContent();
        this.crewLimit = request.getCrewLimit();
        this.chatroomId = 1;
    }
}
