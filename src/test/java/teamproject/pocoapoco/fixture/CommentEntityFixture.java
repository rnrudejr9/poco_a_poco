package teamproject.pocoapoco.fixture;

import teamproject.pocoapoco.domain.entity.Comment;

public class CommentEntityFixture {
    public static Comment get(String userName, String password) {
        return Comment.builder()
                .id(1L)
                .user(UserEntityFixture.get(userName,password))
                .crew(CrewEntityFixture.get(userName,password))
                .comment("comment 입니다.")
                .build();
    }
}