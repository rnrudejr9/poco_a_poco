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


    //운동 검색 By String
    Page<Crew> findBySprotStrContaining(Pageable pageable, String sport);


    //운동 다중 검색 By String
    Page<Crew> findBySprotStrOrSprotStrOrSprotStr(Pageable pageable, String sport, String sport2, String sport3);

    //운동 다중검색2 by String
    @Query("select s from Crew s where s.sprotStr=:sport or s.sprotStr=:sport2 or s.sprotStr=:sport3")
    Page<Crew> findBySprotStr(Pageable pageable, @Param("sport")String sport, @Param("sport2")String sport2, @Param("sport3")String sport3);


    //운동 검색 By Enum
    @Query("select s from Crew s where s.sportEnum=:sport or s.sportEnum=:sport2 or s.sportEnum=:sport3")
    Page<Crew> findBySportEnum(Pageable pageable, @Param("sport")SportEnum sport, @Param("sport2")SportEnum sport2, @Param("sport3")SportEnum sport3);


}
