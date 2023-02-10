package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberResponse;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrewMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 모임 참여자 id

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew; // crew

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public static List<CrewMemberResponse> from(List<CrewMembers> members) {
        return members.stream()
                .map(CrewMemberResponse::of)
                .collect(Collectors.toList());
    }
}