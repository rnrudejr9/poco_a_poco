package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
