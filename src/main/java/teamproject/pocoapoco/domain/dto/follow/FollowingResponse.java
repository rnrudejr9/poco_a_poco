package teamproject.pocoapoco.domain.dto.follow;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.entity.Follow;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FollowingResponse {

    private String userName;
    private String nickName;
    private Boolean status;

    public static FollowingResponse of(Follow follow) {
        return FollowingResponse.builder()
                .userName(follow.getFollowedUser().getUsername())
                .nickName(follow.getFollowedUser().getNickName())
                .status(follow.getStatus())
                .build();
    }
}
