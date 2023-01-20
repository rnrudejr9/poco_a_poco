package teamproject.pocoapoco.fixture;

import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.dto.user.UserJoinRequest;

public class CommentEntityFixture {
    public static Comment get(String userName, String password) {
        return Comment.builder()
                .id(1L)
                .user(UserEntityFixture.get(UserJoinRequest.builder()
                                .userName(userName)
                                .password(password)
                        .build()))
                .crew(CrewEntityFixture.get(1L))
                .comment("comment 입니다.")
                .build();
    }
}