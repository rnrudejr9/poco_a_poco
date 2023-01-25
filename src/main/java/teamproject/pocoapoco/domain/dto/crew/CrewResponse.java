package teamproject.pocoapoco.domain.dto.crew;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class CrewResponse {
    private String message;
    private Long crewId;
}
