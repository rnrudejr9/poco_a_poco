package teamproject.pocoapoco.domain.dto.crew;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CrewAddResponse {
    private String message;
    private Long crewId;
}
