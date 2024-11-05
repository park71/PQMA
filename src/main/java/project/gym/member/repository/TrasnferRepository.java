package project.gym.member.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.TransferEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrasnferRepository extends JpaRepository<TransferEntity, Long> {

    List<TransferEntity> findByStatus(String status, Sort sort);

    Optional<TransferEntity> findById(Long id);


    @Query("SELECT SUM(m.price) FROM TransferEntity m WHERE m.applicationDate >= :startDate AND m.applicationDate < :endDate")
    Integer sumPricesByApplicationDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.price) FROM TransferEntity t WHERE DATE(t.applicationDate) = CURRENT_DATE")
    Integer calculateTodayTransRevenue();
}
