package teamproject.pocoapoco.domain.dto.crew;

import lombok.Getter;
import lombok.Setter;
import teamproject.pocoapoco.enums.SportEnum;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class SportRequest {

    private boolean soccer;
    private boolean jogging;
    private boolean tennis;
}