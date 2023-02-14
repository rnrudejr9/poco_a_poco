package teamproject.pocoapoco.domain.entity.part;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private String title;
    private String body;

    private Integer status;

    private LocalDateTime deletedAt;

    public static Participation deleteParticipation(Participation beforeParticipation, LocalDateTime localDateTime){

        return Participation.builder()
                .id(beforeParticipation.id)
                .user(beforeParticipation.user)
                .crew(beforeParticipation.crew)
                .title(beforeParticipation.title)
                .body(beforeParticipation.body)
                .status(beforeParticipation.status)
                .deletedAt(localDateTime)
                .build();


    }

}
