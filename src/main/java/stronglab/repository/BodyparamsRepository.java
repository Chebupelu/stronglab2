package stronglab.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stronglab.model.Bodyparams;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BodyparamsRepository extends JpaRepository<Bodyparams, Long> {

    // Находит запись атлета за конкретное число (нужно для проверки дубликатов в один день)
    Optional<Bodyparams> findByAthleteIdAndDate(Long athleteId, LocalDate date);

    // Получить всю историю измерений атлета (для графиков)
    List<Bodyparams> findAllByAthleteIdOrderByDateAsc(Long athleteId);
    @Query("SELECT b FROM Bodyparams b WHERE b.athlete.id = :athleteId ORDER BY b.date desc")
    List<Bodyparams> findByAthleteIdOrderByDateDesc(@Param("athleteId") Long athleteId);

    Optional<Bodyparams> findByAthlete_Id(Long athleteId);
}
