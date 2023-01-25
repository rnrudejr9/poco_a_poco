package teamproject.pocoapoco.fixture;

import teamproject.pocoapoco.domain.entity.Crew;

import java.util.ArrayList;

public class CrewEntityFixture {
    public static Crew get(Long crewId) {
        return Crew.builder()
                .id(crewId)
                .strict("서울")
                .title("같이 조깅해요")
                .content("목표는 동네 한바퀴")
                .crewLimit(10)
                .chatroomId(1)
                .likes(new ArrayList<>())
                .build();
    }
}
