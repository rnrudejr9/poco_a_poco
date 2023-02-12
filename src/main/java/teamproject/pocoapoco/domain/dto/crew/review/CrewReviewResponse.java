package teamproject.pocoapoco.domain.dto.crew.review;

import lombok.Builder;
import lombok.Data;
@Data
public class CrewReviewResponse {
    private Long id;
    private String fromUserName;
    private String crewTitle;
    private String crewImage;

    @Builder
    public CrewReviewResponse(Long id, String fromUserName, String crewTitle, String crewImage) {
        this.id = id;
        this.fromUserName = fromUserName;
        this.crewTitle = crewTitle;
        this.crewImage = crewImage;
    }

}
