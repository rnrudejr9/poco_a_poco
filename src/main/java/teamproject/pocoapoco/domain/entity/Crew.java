package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.SportEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
public class Crew extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private Integer chatroomId;

    // participant_id 추후 추가예정

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

    public void of(CrewRequest request) {
        this.strict = request.getStrict();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.crewLimit = request.getCrewLimit();
        this.chatroomId = 1;
    }
}
