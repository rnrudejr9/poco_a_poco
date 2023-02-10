package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Like;
import teamproject.pocoapoco.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByUserAndCrew(User user, Crew crew);
    List<Like> findByCrew(Crew crew);
}
