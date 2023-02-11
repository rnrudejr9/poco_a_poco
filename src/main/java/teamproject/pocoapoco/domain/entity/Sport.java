package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.enums.SportEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Sport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SportEnum sport1;
    private SportEnum sport2;
    private SportEnum sport3;

    public static Sport setSport(SportEnum sport1, SportEnum sport2, SportEnum sport3 ){
        return Sport.builder()
                .sport1(sport1)
                .sport2(sport2)
                .sport3(sport3)
                .build();

    }

}
