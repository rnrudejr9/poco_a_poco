package teamproject.pocoapoco.domain.dto.part;

import lombok.*;
import teamproject.pocoapoco.domain.entity.part.Participation;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartResponse {
    private Integer now;
    private Integer limit;
    private Integer status;
    private String title;
    private String body;


}
