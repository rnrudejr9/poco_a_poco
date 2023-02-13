package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;

import java.util.List;


public interface CrewReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByToUser(User user);

    boolean existsByFromUser(User user);

    long countReviewByToUser(User user);
}