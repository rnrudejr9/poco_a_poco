package teamproject.pocoapoco.domain.dto.part;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartResponse {
    private Integer now;
    private Integer limit;
}
