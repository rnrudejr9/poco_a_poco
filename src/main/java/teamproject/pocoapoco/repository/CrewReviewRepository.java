package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Review;


public interface CrewReviewRepository extends JpaRepository<Review, Long> {
}