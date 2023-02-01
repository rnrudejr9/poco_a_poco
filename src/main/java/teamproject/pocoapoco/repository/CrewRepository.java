package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamproject.pocoapoco.domain.entity.Crew;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    Page<Crew> findByStrictContaining(Pageable pageable, String strict);

    @Query("select s from Crew s where s.sport.soccer=true")
    Page<Crew> findBySportSoccer(Pageable pageable, boolean sport);

    @Query("select s from Crew s where s.sport.tennis=true")
    Page<Crew> findBySportTennis(Pageable pageable, boolean sport);


}
