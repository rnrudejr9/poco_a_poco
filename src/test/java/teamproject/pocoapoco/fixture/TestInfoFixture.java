package teamproject.pocoapoco.fixture;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class TestInfoFixture {
    public static TestInfo get() {
        return TestInfo.builder()
                .userId(1L)
                .crewId(1L)
                .commentId(1L)
                .likeId(1L)
                .alarmId(1L)
                .userUserId("userId")
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
        private Long crewId;
        private Long userId;
        private Long commentId;
        private Long likeId;
        private Long alarmId;
        private String userName;
        private String userUserId;
        private String password;
        private String title;
        private String body;
    }
}
