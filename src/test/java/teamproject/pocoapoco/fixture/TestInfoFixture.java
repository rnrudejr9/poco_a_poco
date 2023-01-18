package teamproject.pocoapoco.fixture;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class TestInfoFixture {
    public static TestInfo get() {
        return TestInfo.builder()
                .userId(1L)
                .postId(1L)
                .commentId(1L)
                .likeId(1L)
                .alarmId(1L)
                .userName("name")
                .password("password")
                .title("title")
                .body("body")
                .build();
    }

    @Getter
    @Setter
    @Builder
    public static class TestInfo {
        private Long postId;
        private Long userId;
        private Long commentId;
        private Long likeId;
        private Long alarmId;
        private String userName;
        private String password;
        private String title;
        private String body;
    }
}
