package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamproject.pocoapoco.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);

    Optional<User> findById(Long userId);

    Optional<User> findByUserName(String userName);

    @Query("select m from User m where m.userId=?1")
    User findOauthUser(String username);
}
