package teamproject.pocoapoco.domain.dto.part;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PartJoinDto {
    private Long crewId;
    private Long userId;
    private String userName;
    private String title;
    private String body;
}
