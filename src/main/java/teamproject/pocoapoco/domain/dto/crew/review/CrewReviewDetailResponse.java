package teamproject.pocoapoco.domain.dto.crew.review;

import lombok.*;

import java.util.Map;
@Data
public class CrewReviewDetailResponse {
    private Long id;
    private String fromUserName;
    private String crewTitle;
    private String reviewContext;
//    private Map<String, String> reviews;

    @Builder
    public CrewReviewDetailResponse(Long id, String fromUserName, String crewTitle, String reviewContext/*, Map<String, String> reviews*/) {
        this.id = id;
        this.fromUserName = fromUserName;
        this.crewTitle = crewTitle;
        this.reviewContext = reviewContext;
//        this.reviews = reviews;
    }
}
