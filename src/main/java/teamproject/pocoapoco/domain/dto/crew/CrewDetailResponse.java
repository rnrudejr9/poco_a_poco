package teamproject.pocoapoco.domain.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

@Builder
@AllArgsConstructor
@Getter
public class CrewDetailResponse {

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;

    public static CrewDetailResponse fromEntity(Crew crew) {
        return CrewDetailResponse.builder()
                .strict(crew.getStrict())
                .title(crew.getTitle())
                .content(crew.getContent())
                .crewLimit(crew.getCrewLimit())
                .build();
    }

}
