package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;

@Getter
@Setter
public class CrewReviewRequest2 {

    private Crew crewId;
    private Long fromUserId;
    private Long toUserId;
    private Double mannerScore;
    private String userReview;

}
