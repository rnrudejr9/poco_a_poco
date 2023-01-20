package teamproject.pocoapoco.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentRequest {
    private String comment;

    public Comment toEntity(User user, Crew crew) {
        return Comment.builder()
                .comment(this.comment)
                .crew(crew)
                .user(user)
                .build();
    }
}

