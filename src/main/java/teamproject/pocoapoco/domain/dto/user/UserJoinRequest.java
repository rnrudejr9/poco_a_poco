package teamproject.pocoapoco.domain.dto.user;


import lombok.*;
import org.springframework.context.annotation.Bean;
import teamproject.pocoapoco.enums.SportEnum;

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
    private SportEnum sport1;
    private SportEnum sport2;
    private SportEnum sport3;

}
