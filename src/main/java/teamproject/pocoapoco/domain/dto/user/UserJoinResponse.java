package teamproject.pocoapoco.domain.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserJoinResponse {

    private String userName;
    private String message;

}
