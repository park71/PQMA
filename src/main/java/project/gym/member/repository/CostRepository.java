package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.CostEntity;

import java.time.LocalDate;

@Repository
public interface CostRepository extends JpaRepository<CostEntity, Long> {

    @Query("SELECT SUM(c.pay) FROM CostEntity c WHERE c.applicantDate >= :startDate AND c.applicantDate < :endDate")
    Integer sumPricesByApplicationDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
