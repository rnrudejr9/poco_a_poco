package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.enums.SportEnum;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    //지역 검색 By String
    Page<Crew> findByStrictContaining(Pageable pageable, String strict);

    //운동 검색 By Enum
    @Query("select s from Crew s where s.sportEnum=:sport or s.sportEnum=:sport2 or s.sportEnum=:sport3")
    Page<Crew> findBySportEnum(Pageable pageable, @Param("sport")SportEnum sport, @Param("sport2")SportEnum sport2, @Param("sport3")SportEnum sport3);

}
