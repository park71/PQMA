package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.CloserEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CloserRepository extends JpaRepository<CloserEntity, Long> {

    @Query("SELECT c.revenue FROM CloserEntity c WHERE c.dateday = :dateday")
    Integer findRevenueByDate(@Param("dateday") LocalDate dateday);


    List<CloserEntity> findByDateday(LocalDate dateday);
}
