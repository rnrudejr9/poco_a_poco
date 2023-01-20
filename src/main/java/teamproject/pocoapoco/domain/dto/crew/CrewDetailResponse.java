package teamproject.pocoapoco.domain.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrewDetailResponse {

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;

    public static CrewDetailResponse of(Crew crew) {
        return CrewDetailResponse.builder()
                .strict(crew.getStrict())
                .title(crew.getTitle())
                .content(crew.getContent())
                .crewLimit(crew.getCrewLimit())
                .build();
    }

}
