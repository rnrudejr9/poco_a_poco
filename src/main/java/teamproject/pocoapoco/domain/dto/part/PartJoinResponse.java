package teamproject.pocoapoco.domain.dto.part;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartJoinResponse {
    private Integer now;
    private Integer limit;
    private Integer status;
    private String crewTitle;
    private String title;
    private String body;
    private String joinUserName;
    private String writerUserName;
}
