package teamproject.pocoapoco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmType {
    LIKE_CREW("좋아요를 눌렀습니다."),
    ADD_COMMENT("댓글을 남겼습니다."),
    LIKE_COMMENT("좋아요를 눌렀습니다."),
    ;
    String text;
}
