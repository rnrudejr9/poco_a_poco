package teamproject.pocoapoco.domain.dto.crew;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrewReviewRequest {

    private Long crewId;
    private Long fromUserId;
    private List<Long> toUserId;
    private List<Double> mannerScore;
    private List<String> userReview;
}
