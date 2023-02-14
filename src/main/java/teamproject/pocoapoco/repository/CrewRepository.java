package teamproject.pocoapoco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.enums.StrictEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    //지역 검색 By String
    Page<Crew> findByStrictContaining(Pageable pageable, String strict);

    //운동 검색 By Enum
    @Query("select s from Crew s where s.sportEnum=:sport or s.sportEnum=:sport2 or s.sportEnum=:sport3")
    Page<Crew> findBySportEnum(Pageable pageable, @Param("sport") SportEnum sport, @Param("sport2") SportEnum sport2, @Param("sport3") SportEnum sport3);


    Page<Crew> findByStrictContainsAndSportEnumIn(String strict, List<SportEnum> sportEnum, Pageable pageable);

    Long countBy();

    Long countByStrictContaining(@Param("strictValue") String strictValue);

    @Query("select count(s.id) from Crew s where s.sportEnum=:sport")
    Long countBySportEnum(@Param("sport") SportEnum sport);

    List<Crew> findByDeletedAt(@Nullable LocalDateTime deletedAt, Pageable pageable);


    List<Crew> findByDeletedAtAndStrictContaining(@Nullable LocalDateTime deletedAt, String strict, Pageable pageable);

    // 참여 중인 모임
    Page<Crew> findByParticipationsIn(List<Participation> participation, Pageable pageable);
    long countByParticipationsIn(List<Participation> participation);
}
