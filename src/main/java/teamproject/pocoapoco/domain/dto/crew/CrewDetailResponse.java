package teamproject.pocoapoco.domain.dto.crew;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Crew;

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
    private String userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
//    private String sport;

    public static CrewDetailResponse of(Crew crew) {
        return CrewDetailResponse.builder()
                .id(crew.getId())
                .strict(crew.getStrict())
                .title(crew.getTitle())
                .content(crew.getContent())
                .crewLimit(crew.getCrewLimit())
                .userId(crew.getUser().getUserId())
                .userName(crew.getUser().getUsername())
                .createdAt(crew.getCreatedAt())
                .lastModifiedAt(crew.getLastModifiedAt())
//                .sport(crew.getSport().getName())
                .build();
    }

}
