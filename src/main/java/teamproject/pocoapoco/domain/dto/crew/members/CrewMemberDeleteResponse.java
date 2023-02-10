package teamproject.pocoapoco.domain.dto.crew.members;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewMemberDeleteResponse {
    private String message;
    private Long id;

    public static CrewMemberDeleteResponse of(Long id) {
        return new CrewMemberDeleteResponse("모임에 나가셨습니다.", id);
    }
}