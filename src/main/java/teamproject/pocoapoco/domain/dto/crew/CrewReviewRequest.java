package teamproject.pocoapoco.domain.dto.crew;

import lombok.Getter;
import lombok.Setter;
import teamproject.pocoapoco.domain.entity.Review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class CrewReviewRequest {

    private List<Long> crewId;
    private List<Long> fromUserId;
    private List<Long> toUserId;
    private List<Double> mannerScore;
    private List<String> userReview;

}
