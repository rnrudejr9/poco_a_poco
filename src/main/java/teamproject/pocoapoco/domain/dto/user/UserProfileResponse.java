package teamproject.pocoapoco.domain.dto.user;

import lombok.Builder;
import lombok.Getter;
import teamproject.pocoapoco.domain.entity.User;

@Builder
@Getter
public class UserProfileResponse {
    private String nickName;
    private String address;
    private Boolean likeSoccer;
    private Boolean likeJogging;
    private Boolean likeTennis;

    public static UserProfileResponse fromEntity(User user){

        return UserProfileResponse.builder()
                .nickName(user.getNickName())
                .address(user.getAddress())
                .likeSoccer(user.getSport().isSoccer())
                .likeJogging(user.getSport().isJogging())
                .likeTennis(user.getSport().isTennis())
                .build();
    }
}
