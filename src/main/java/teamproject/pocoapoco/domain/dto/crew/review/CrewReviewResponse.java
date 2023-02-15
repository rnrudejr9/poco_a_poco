package teamproject.pocoapoco.domain.dto.crew.review;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Review;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrewReviewResponse {
    private Long id;
    private String fromUserName;
    private String crewTitle;
    private String crewImage;
    private String fromUserImagePath;

    public static CrewReviewResponse of(Review review) {
        return CrewReviewResponse.builder()
                .id(review.getId())
                .fromUserName(review.getFromUser().getNickName())
                .crewTitle(review.getCrew().getTitle())
                .crewImage(review.getCrew().getImagePath())
                .fromUserImagePath(review.getFromUser().getImagePath())
                .build();
    }

}
