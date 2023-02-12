package teamproject.pocoapoco.domain.dto.crew;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewRequest {

    private List<Long> crewId;
    private List<Long> fromUserId;
    private List<Long> toUserId;
    private List<Double> userMannerScore;
    private List<String> userReview;

}
