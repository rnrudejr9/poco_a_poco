package teamproject.pocoapoco.domain.dto.alarm;

import lombok.*;
import org.springframework.lang.Nullable;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.AlarmType;
import teamproject.pocoapoco.repository.UserRepository;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AlarmResponse {
    private Long id;
    private AlarmType alarmType;
    private String username;
    private String fromUserName;
    private String imagePath;
    private Long targetId;
    @Nullable
    private String targetName;
    private String text;
    private LocalDateTime createdAt;
    private boolean readOrNot;



    public static AlarmResponse fromEntity(Alarm alarm, String imagePath) {

        return AlarmResponse.builder()
                .id(alarm.getId())
                .username(alarm.getUser().getUsername())
                .alarmType(alarm.getAlarmType())
                .fromUserName(alarm.getFromUserName())
                .targetId(alarm.getTargetId())
                .targetName(alarm.getTargetName())
                .text(alarm.getMassage())
                .createdAt(alarm.getCreatedAt())
                .readOrNot(alarm.getReadOrNot())
                .imagePath(imagePath)
                .build();
    }
}

