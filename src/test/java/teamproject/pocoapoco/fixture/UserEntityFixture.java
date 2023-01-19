package teamproject.pocoapoco.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.enums.UserRole;

@AllArgsConstructor
@Getter
public class UserEntityFixture {


    public static User get(UserJoinRequest userJoinRequest) {
        return User.builder()
                .id(1L)
                .userId(userJoinRequest.getUserId())
                .userName(userJoinRequest.getUserName())
                .password(userJoinRequest.getPassword())
                .address(userJoinRequest.getAddress())
                .sport(Sport.setSport(userJoinRequest.getLikeSoccer(), userJoinRequest.getLikeJogging(), userJoinRequest.getLikeTennis()))
                .role(UserRole.ROLE_USER)
                .build();
    }
    public static User getADMIN(UserJoinRequest userJoinRequest){
        return User.builder()
                .id(1L)
                .userId(userJoinRequest.getUserId())
                .userName(userJoinRequest.getUserName())
                .password(userJoinRequest.getPassword())
                .address(userJoinRequest.getAddress())
                .sport(Sport.setSport(userJoinRequest.getLikeSoccer(), userJoinRequest.getLikeJogging(), userJoinRequest.getLikeTennis()))
                .role(UserRole.ROLE_ADMIN)
                .build();
    }
}