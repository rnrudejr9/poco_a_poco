package teamproject.pocoapoco.domain.dto.manage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserManageResponse {

    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private Integer mannerScore;



    public static UserManageResponse fromEntity(User user){
        return UserManageResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .mannerScore(user.getManner())
                .build();
    }
}
