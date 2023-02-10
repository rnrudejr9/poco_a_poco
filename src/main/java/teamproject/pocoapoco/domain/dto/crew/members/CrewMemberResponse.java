package teamproject.pocoapoco.domain.dto.crew.members;

import lombok.*;
import teamproject.pocoapoco.domain.entity.CrewMembers;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrewMemberResponse {
    private Long id;
    private Long crewId;
    private String userName;
    private int joinCheck;

    public static CrewMemberResponse of(CrewMembers members) { // 모임 리스트 출력 시 사용 = crew_members에 저장된 정보라는 의미
        return CrewMemberResponse.builder()
                .id(members.getId())
                .crewId(members.getCrew().getId())
                .userName(members.getUser().getUsername())
                .joinCheck(members.getUser().getMembers() != null ? 1:0) // 모임 리스트의 유저를 가져오는데, member = null일수 없음.
                .build();
    }
}