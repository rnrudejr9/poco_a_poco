package teamproject.pocoapoco.fixture;

import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;

public class UserEntityFixture {
    public static User get(String userName, String password) {
        return User.builder()
                .id(1L)
                .userName(userName)
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();
    }
    public static User getADMIN(String userName, String password){
        return User.builder()
                .id(1L)
                .userName(userName)
                .password(password)
                .role(UserRole.ROLE_ADMIN)
                .build();
    }
}