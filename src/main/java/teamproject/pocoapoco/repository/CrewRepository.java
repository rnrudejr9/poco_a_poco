package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    Page<Crew> findByStrictContaining(Pageable pageable, String strict);

    Page<Crew> findBySportIs(Pageable pageable, String umm);
}
