package teamproject.pocoapoco.domain.dto.manage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.part.Participation;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrewManageResponse {

    private Long id;
    private String title;

    private String strict;

    private Integer crewLimit;
    private Integer currentParticipants; // user에서 crew아이디가 그것인 사람 수

    private String status;

    public static CrewManageResponse fromEntity(Crew crew){
        String status = null;

        Participation participation = crew.getParticipations().get(0);
        if(participation.getStatus() == 1){
            status = "승인대기";
        } else if (participation.getStatus() == 2) {
            status = "모집중";

        } else if (participation.getStatus() == 3){
            status = "모집종료";
        } else{
            status = "기타";
        }


        return CrewManageResponse.builder()
                .id(crew.getId())
                .title(crew.getTitle())
                .strict(crew.getStrict())
                .crewLimit(crew.getCrewLimit())
                .currentParticipants(crew.getParticipations().size())
                .status(status)
                .build();

    }

}
