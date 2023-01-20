package teamproject.pocoapoco.domain.user;

import lombok.Builder;
import lombok.Getter;
import teamproject.pocoapoco.domain.entity.User;

@Builder
@Getter
public class UserProfileResponse {
    private String userName;
    private String address;
    private Boolean likeSoccer;
    private Boolean likeJogging;
    private Boolean likeTennis;

    public static UserProfileResponse fromEntity(User user){

        return UserProfileResponse.builder()
                .userName(user.getUsername())
                .address(user.getAddress())
                .likeSoccer(user.getSport().isSoccer())
                .likeJogging(user.getSport().isJogging())
                .likeJogging(user.getSport().isTennis())
                .build();
    }
}
