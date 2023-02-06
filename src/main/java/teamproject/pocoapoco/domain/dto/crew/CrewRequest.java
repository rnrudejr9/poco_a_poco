package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
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


    public Crew toEntity(User user) {
        return Crew.builder()
                .strict(this.strict)
                .title(this.title)
                .content(this.content)

                .crewLimit(this.crewLimit)
                .chatroomId(null)
                .user(user)

                //crew 종목 검색 test
                .sprotStr("테니스")
                .interestSport(InterestSport.TENNIS)
                .sportEnum(SportEnum.TENNIS)
                .build();
    }
}
