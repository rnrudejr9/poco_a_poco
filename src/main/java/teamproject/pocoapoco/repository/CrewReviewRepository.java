package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;

import java.util.List;


public interface CrewReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByToUser(User user);

    List<Review> findByCrewId(Long crewId);

    long countReviewByToUser(User user);
    Review findReviewByCrewAndToUser(Crew crew, User user);
}