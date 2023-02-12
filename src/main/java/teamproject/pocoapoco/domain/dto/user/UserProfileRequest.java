package teamproject.pocoapoco.domain.dto.user;

import lombok.*;
import teamproject.pocoapoco.enums.SportEnum;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserProfileRequest {

    private String nickName;
    private String password;
    private String passwordConfirm;
    private String address;
    private List<String> sportList;
    private Boolean sportListChange = false;


}
