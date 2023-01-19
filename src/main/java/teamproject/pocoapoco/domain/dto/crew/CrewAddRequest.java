package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CrewAddRequest {

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
                .chatroomId(1) // 채팅룸 Entity를 따로 make?
                .user(user)
                .build();
    }
}
