package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.CrewMembers;
import teamproject.pocoapoco.domain.entity.User;

import java.util.List;

public interface CrewMemberRepository extends JpaRepository<CrewMembers, Long> {
    List<CrewMembers> findMembersByCrewId(Long crewId);
    void deleteByUserAndCrew(User user, Crew crew);
}