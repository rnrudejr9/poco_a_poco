package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.SportEnum;

import java.util.List;


@Getter
@Setter
public class CrewSportRequest {

    private String strict;
    private List<String> sportsList;

}
