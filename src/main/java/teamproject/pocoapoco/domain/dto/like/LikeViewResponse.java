package teamproject.pocoapoco.domain.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeViewResponse {
    private int likeCheck;
    private int count;
    private String userName;
}