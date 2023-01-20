package teamproject.pocoapoco.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import teamproject.pocoapoco.domain.entity.Comment;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long crewId;
//    private Long parentId;

    private LocalDateTime createdAt;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUsername())
                .crewId(comment.getCrew().getId())
//                .parentId(comment.getParent().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

