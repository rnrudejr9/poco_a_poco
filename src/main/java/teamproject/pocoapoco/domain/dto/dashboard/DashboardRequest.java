package teamproject.pocoapoco.domain.dto.dashboard;

import lombok.*;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.enums.StrictEnum;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class DashboardRequest {

    String strict;
    String sport;
}
