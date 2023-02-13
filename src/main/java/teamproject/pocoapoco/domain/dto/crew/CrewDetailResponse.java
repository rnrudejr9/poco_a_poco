package teamproject.pocoapoco.domain.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.enums.SportEnum;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrewDetailResponse {
    private Long id;
    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private Long userId;
    private String userName;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private SportEnum sportEnum;
    private Long chatRoomId;
    private String imagePath;
    private String date;
    public static CrewDetailResponse of(Crew crew) {
        return CrewDetailResponse.builder()
                .id(crew.getId())
                .strict(crew.getStrict())
                .title(crew.getTitle())
                .content(crew.getContent())
                .crewLimit(crew.getCrewLimit())
                .userId(crew.getUser().getId())
                .nickName(crew.getUser().getNickName())
                .userName(crew.getUser().getUsername())
                .createdAt(crew.getCreatedAt())
                .lastModifiedAt(crew.getLastModifiedAt())
                .imagePath(crew.getImagePath())
                .chatRoomId(crew.getChatRoom().getRoomId())
                .sportEnum(crew.getSportEnum())
                .date(crew.getDatepick()+" "+crew.getTimepick())
                .build();
    }

}
