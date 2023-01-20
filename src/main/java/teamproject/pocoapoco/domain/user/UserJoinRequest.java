package teamproject.pocoapoco.domain.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequest {

    private String userId;
    private String userName;
    private String password;
    private String passwordConfirm;
    private String address;
    private Boolean likeSoccer;
    private Boolean likeJogging;
    private Boolean likeTennis;


}
