package teamproject.pocoapoco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Sport;

public interface SportRepository extends JpaRepository<Sport, Long> {

}
