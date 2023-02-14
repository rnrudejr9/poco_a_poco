package teamproject.pocoapoco.domain.dto.part;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.part.Participation;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartJoinResponse {
    private long crewId;
    private Integer now;
    private Integer limit;
    private Integer status;
    private String crewTitle;
    private String title;
    private String body;
    private String joinUserName;
    private String writerUserName;
    private Long joinUserId;
}
