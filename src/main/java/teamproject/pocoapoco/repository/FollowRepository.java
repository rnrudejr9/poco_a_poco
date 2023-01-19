package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Follow;
import teamproject.pocoapoco.domain.entity.User;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {

    Optional<Follow> findByFollowingUserIdAndFollowedUserId(Long followingUserID, Long followedUserId);

    Integer countByFollowingUserId(Long followingUserId);
    Integer countByFollowedUserId(Long followedUserId);
}
