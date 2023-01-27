package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.User;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findByUser(User user, Pageable pageable);

//    Optional<Alarm> findByTargetIdAndFromUserId(Long postId, Long userId);
}
