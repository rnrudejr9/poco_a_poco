package teamproject.pocoapoco.domain.dto.user;

import lombok.Builder;
import lombok.Getter;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.SportEnum;

@Builder
@Getter
public class UserProfileResponse {
    private Long userId;
    private String nickName;
    private String address;
    private String sportValue1;
    private String sportValue2;
    private String sportValue3;
    private double mannerScore;
    private String imagePath;

    public static UserProfileResponse fromEntity(User user){

        String sport1 = null;
        String sport2= null;
        String sport3 = null;

        if(user.getSport().getSport1()!=null){
            sport1 = user.getSport().getSport1().getValue();
        }

        if(user.getSport().getSport2()!=null){
            sport2 = user.getSport().getSport2().getValue();
        }

        if(user.getSport().getSport3()!=null){
            sport3= user.getSport().getSport3().getValue();
        }

        return UserProfileResponse.builder()
                .userId(user.getId())
                .nickName(user.getNickName())
                .address(user.getAddress())
                .sportValue1(sport1)
                .sportValue2(sport2)
                .sportValue3(sport3)
                .mannerScore(user.getMannerScore())
                .imagePath(user.getImagePath())
                .build();
    }
}
