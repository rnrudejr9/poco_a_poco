package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.SportEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CrewRequest {

    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private String datepick;
    private String timepick;
    private String chooseSport;
    private String imagePath;


    public Crew toEntity(User user) {
        return Crew.builder()
                .strict(this.strict)
                .title(this.title)
                .content(this.content)
                .crewLimit(this.crewLimit)
                .datepick(this.datepick)
                .timepick(this.timepick)
                .imagePath(this.imagePath)
                .chatRoom(ChatRoom.builder().name(title).user(user).build())
                .sportEnum(of(chooseSport))
                .user(user)
                .build();
    }

    public SportEnum of(String value){
        for(SportEnum sportEnum : SportEnum.values()){
            if(sportEnum.getValue().equals(value)){
                return sportEnum;
            }
        }
        return null;
    }


}
