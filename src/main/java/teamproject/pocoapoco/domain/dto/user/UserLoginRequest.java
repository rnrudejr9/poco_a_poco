package teamproject.pocoapoco.domain.dto.user;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
public class UserLoginRequest {
    private String userName;
    private String password;
}
