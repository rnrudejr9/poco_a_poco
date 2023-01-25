package teamproject.pocoapoco.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLogoutRequest {
    private String refreshToken;
    private String accessToken;
}
