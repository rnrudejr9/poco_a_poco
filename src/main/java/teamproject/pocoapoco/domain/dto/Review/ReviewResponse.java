package teamproject.pocoapoco.domain.dto.Review;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Sport;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewResponse {

    private Long crewId;
    private Long joinUserId;
    private String joinUserNickName;
    private Double userMannerScore;
    private List<Sport> sports;

}
