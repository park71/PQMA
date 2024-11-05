package project.gym.member.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.RestEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestRepository extends JpaRepository<RestEntity, Integer> {

    List<RestEntity> findByName(String name);

    Optional<RestEntity> findById(Long id);


    @Query("SELECT SUM(m.lockprice) FROM RestEntity m WHERE m.applicationDate >= :startDate AND m.applicationDate < :endDate")
    Integer sumPricesByApplicationDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<RestEntity> findByPhone(String phone);

    List<RestEntity> findByStatus(String status, Sort sort);

    List<RestEntity> findAllByPhone(String phone);

    List<RestEntity> findByNameContaining(String name);


    @Query("SELECT SUM(r.lockprice) FROM RestEntity r WHERE DATE(r.applicationDate) = CURRENT_DATE")
    Integer calculateTodayRestRevenue();
}
