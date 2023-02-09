package teamproject.pocoapoco.domain.dto.user;


import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserJoinRequest {

    private String userName;
    private String nickName;
    private String password;
    private String passwordConfirm;
    private String address;
    private String email;
    @Builder.Default
    private Boolean likeSoccer = false;
    @Builder.Default
    private Boolean likeJogging = false;
    @Builder.Default
    private Boolean likeTennis = false;

}
