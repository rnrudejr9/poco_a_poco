package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private boolean soccer;
    private boolean jogging;
    private boolean tennis;

    public static Sport setSport(boolean likeSoccer, boolean likeJogging, boolean likeTennis){
        return Sport.builder()
                .soccer(likeSoccer)
                .jogging(likeJogging)
                .tennis(likeTennis)
                .build();

    }

}
