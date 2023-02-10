package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;

import java.util.List;


@Getter
@Setter
public class CrewSportRequest {

    private String strict;
    private List<String> sportsList;
    private boolean loginStatus;
}
