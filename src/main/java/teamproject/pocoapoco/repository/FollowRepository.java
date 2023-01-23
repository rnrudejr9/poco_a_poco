package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Follow;
import teamproject.pocoapoco.domain.entity.User;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {

    Optional<Follow> findByFollowingUserIdAndFollowedUserId(Long followingUserID, Long followedUserId);

    Integer countByFollowingUserId(Long followingUserId);
    Integer countByFollowedUserId(Long followedUserId);

    Page<Follow> findByFollowingUserId(Pageable pageable,Long followingUserId);
    Page<Follow> findByFollowedUserId(Pageable pageable,Long followedUserId);
}
