package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;

import java.util.List;


public interface CrewReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByToUser(User user, Pageable pageable);

    List<Review> findByCrewId(Long crewId);

    long countReviewByToUser(User user);
    List<Review> findAllByCrewAndToUser(Crew crew, User user);
}