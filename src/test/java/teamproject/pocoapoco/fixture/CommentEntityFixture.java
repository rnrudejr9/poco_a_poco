package teamproject.pocoapoco.fixture;

import teamproject.pocoapoco.domain.entity.Comment;

public class CommentEntityFixture {
    public static Comment get(String userName, String password) {
        Comment comment = new Comment();
        return comment;
    }
}