package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CrewRequest {

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;

    public Crew toEntity(User user) {
        return Crew.builder()
                .strict(this.strict)
                .title(this.title)
                .content(this.content)

                .crewLimit(this.crewLimit)
                .user(user)
                .build();
    }
}
