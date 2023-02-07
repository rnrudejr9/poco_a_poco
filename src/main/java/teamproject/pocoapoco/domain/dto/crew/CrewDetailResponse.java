package teamproject.pocoapoco.domain.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.SportEnum;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrewDetailResponse {
    private Long id;
    private String strict;
    private String title;
    private String content;
    private Integer crewLimit;
    private String userName;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    //crew 종목 검색 test
    private String sprotStr;
    private InterestSport interestSport;
    private SportEnum sportEnum;

    private String imagePath;

    public static CrewDetailResponse of(Crew crew) {
        return CrewDetailResponse.builder()
                .id(crew.getId())
                .strict(crew.getStrict())
                .title(crew.getTitle())
                .content(crew.getContent())
                .crewLimit(crew.getCrewLimit())
                .nickName(crew.getUser().getNickName())
                .userName(crew.getUser().getUsername())
                .createdAt(crew.getCreatedAt())
                .lastModifiedAt(crew.getLastModifiedAt())
                .imagePath(crew.getImagePath())

                //crew 종목 검색 test
                .sprotStr(crew.getSprotStr())
                .interestSport(crew.getInterestSport())
                .sportEnum(crew.getSportEnum())
                .build();
    }

}
