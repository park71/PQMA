package project.gym.member.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.PTContractEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PTContractRepository extends JpaRepository<PTContractEntity, Long> {

    Optional<PTContractEntity> findByPhone(String phone);

    @Query("SELECT m FROM PTContractEntity m WHERE DATE(m.applicationDate) = CURRENT_DATE")
    List<PTContractEntity> findTodayRegistration();

    @Query("SELECT SUM(m.price) FROM PTContractEntity m WHERE m.applicationDate >= :startDate AND m.applicationDate < :endDate")
    Integer sumPricesByApplicationDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT SUM(p.price) FROM PTContractEntity p WHERE p.applicationDate >= :startDate AND p.applicationDate < :endDate AND p.coach = '환불처리완료'")
    Integer sumRefundedPricesByApplicationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<PTContractEntity> findById(Long id);

    List<PTContractEntity> findByStatus(String status, Sort sort);

    List<PTContractEntity> findByStatusAndCoachNot(String status,String coach,Sort sort);

    List<PTContractEntity> findByNameContaining(String name);


    List<PTContractEntity> findByName(String name);

    List<PTContractEntity> findByCoachAndStatus(String coach, String status, Sort sort);

    @Query("SELECT SUM(p.price) FROM PTContractEntity p WHERE DATE(p.applicationDate) = CURRENT_DATE")
    Integer calculateTodayPTRevenue();

}
