package teamproject.pocoapoco.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteResponse {
    private String message;
    private Long id;

    public static CommentDeleteResponse of(Long commentId) {
        return new CommentDeleteResponse("댓글 삭제 완료", commentId);
    }
}
