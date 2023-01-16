package teamproject.pocoapoco.domain.entity;

import lombok.Getter;
import teamproject.pocoapoco.enums.AlarmType;

import javax.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private AlarmType alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

}
