package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CrewRequest {

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private String datepick;
    private String timepick;
    private String imagePath;
    private String chooseSport;


    public Crew toEntity(User user) {
        return Crew.builder()
                .strict(this.strict)
                .title(this.title)
                .content(this.content)
                .crewLimit(this.crewLimit)

                .datepick(this.datepick)
                .timepick(this.timepick)

                .user(user)
                .sprotStr(this.chooseSport)
                .imagePath(this.imagePath)

                .build();
    }
}
