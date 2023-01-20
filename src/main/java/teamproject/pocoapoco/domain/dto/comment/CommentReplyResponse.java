package teamproject.pocoapoco.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Comment;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentReplyResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long crewId;
    private Long parentId;

    private LocalDateTime createdAt;

    public static CommentReplyResponse of(Comment comment) {
        return CommentReplyResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUsername())
                .crewId(comment.getCrew().getId())
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent().getId())
                .build();
    }
}

