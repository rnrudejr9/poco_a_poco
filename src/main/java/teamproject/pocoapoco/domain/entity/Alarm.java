package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.enums.AlarmType;

import javax.persistence.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Entity
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Crew의 주인

    private String fromUserName;
    private Long targetCrewId;
    private String massage;
    private boolean readOrNot;
    private AlarmType alarmType;

    public static Alarm toEntity(User user, Crew crew, AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(crew.getUser())
                .alarmType(alarmType)
                .fromUserName(user.getNickName())
                .targetCrewId(crew.getId())
                .massage(comment)
                .build();
        return alarm;
    }

    public boolean getReadOrNot() {
        return readOrNot;
    }
    public void setReadOrNot() {
        this.readOrNot = true;
    }
}
