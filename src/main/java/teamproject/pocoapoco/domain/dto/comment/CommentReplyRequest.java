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
public class CommentReplyRequest {
    private String comment;

    public Comment toEntity(User user, Crew crew, Comment parentComment) {
        return Comment.builder()
                .comment(this.comment)
                .user(user)
                .crew(crew)
                .parent(parentComment)
                .build();
    }
}

