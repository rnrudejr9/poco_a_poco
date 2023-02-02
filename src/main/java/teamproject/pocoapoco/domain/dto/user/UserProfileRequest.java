package teamproject.pocoapoco.domain.dto.user;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserProfileRequest {

    private String userName;
    private String password;
    private String passwordConfirm;
    private String address;
    private Boolean likeSoccer;
    private Boolean likeJogging;
    private Boolean likeTennis;


}
