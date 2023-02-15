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
    private User user;

    private String fromUserName;
    private Long targetId; //어디에 팔로우를 했는지
    private String targetName; //프로필 조회가 userName으로 되어있음
    private String massage;
    private boolean readOrNot;
    private AlarmType alarmType;

    public static Alarm toEntity(User user, Crew crew, AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(crew.getUser())
                .alarmType(alarmType)
                .fromUserName(user.getUsername())
                .targetId(crew.getId())
                .massage(comment)
                .build();
        return alarm;
    }

    public static Alarm toEntityFromFinishCrew(User user, Crew crew, AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(user)
                .alarmType(alarmType)
                .fromUserName(crew.getUser().getUsername())
                .targetId(crew.getId())
                .massage(comment)
                .build();
        return alarm;
    }

    public static Alarm toEntityFromComment(User user, Crew crew, Comment commentUser , AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(commentUser.getUser())
                .alarmType(alarmType)
                .fromUserName(user.getUsername())
                .targetId(crew.getId())
                .massage(comment)
                .build();
        return alarm;
    }

    public static Alarm toEntityFromFollow(User user, User followingUser, AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(user)
                .alarmType(alarmType)
                .fromUserName(followingUser.getUsername())
                .targetId(followingUser.getId())
                .targetName(followingUser.getUsername())
                .massage(comment)
                .build();
        return alarm;
    }

    public static Alarm toEntityFromReview(User toUser, User fromUser,Review review ,AlarmType alarmType, String comment) {
        Alarm alarm = Alarm.builder()
                .user(toUser)
                .alarmType(alarmType)
                .fromUserName(fromUser.getUsername())
                .targetId(review.getId())
                .targetName(toUser.getUsername())
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
