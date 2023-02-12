package teamproject.pocoapoco.repository.part;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.part.Participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation,Long> {
    Boolean existsByCrewAndAndUser(Crew crew, User user);
    List<Participation> findByUser(User user);
    Optional<Participation> findByCrewAndUser(Crew crew,User user);
    List<Participation> findByCrew(Crew crew);
    List<Participation> findByStatusAndUser(Integer status, User user);
}
