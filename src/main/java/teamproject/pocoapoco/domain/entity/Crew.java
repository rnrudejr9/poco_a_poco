package teamproject.pocoapoco.domain.entity;

import org.springframework.lang.Nullable;
import teamproject.pocoapoco.enums.InterestSport;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Set;

@Entity
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String strict;
    private String title;
    private String content;
    private Integer limit;
    private Integer chatroom_id;
    // participant_id 추후 추가예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
