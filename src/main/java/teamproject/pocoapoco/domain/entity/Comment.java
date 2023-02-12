package teamproject.pocoapoco.domain.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import teamproject.pocoapoco.domain.dto.comment.ui.CommentViewResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Where(clause = "deleted_at is null")
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    Crew crew;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Setter
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public void setComment(String comment) {
        this.comment = comment;
    }
    public static List<CommentViewResponse> from(List<Comment> comments) {
        return comments.stream()
                .map(CommentViewResponse::of)
                .collect(Collectors.toList());
    }
}
