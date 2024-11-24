package project.gym.member.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.MemberEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 장기미출석자인 회원만 조회
    List<MemberEntity> findByStat(String stat);
    // 장기미출석자인 회원만 조회
//    List<MemberEntity> findAllByStat(String stat, Sort sort);

    List<MemberEntity> findByMemendBefore(LocalDate date);

    Optional<MemberEntity> findByPhone(String phone);

    List<MemberEntity> findByCreditAndApplicationDateBetween(String credit, LocalDateTime startOfDay, LocalDateTime endOfDay);


    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE m.applicationDate >= :startDate AND m.applicationDate < :endDate AND m.coach = '환불처리완료'")
    Integer sumRefundedPricesByApplicationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE m.applicationDate = :date")
    Integer sumPricesByApplicationDate(@Param("date") LocalDateTime date);


    Optional<MemberEntity> findByLocknum(Integer locknum);

//    Optional<MemberEntity> findByPhoneEndingWith(String phoneSuffix);

    Optional<MemberEntity> findByName(String name);

    MemberEntity findOptionalByName(String name);

    List<MemberEntity> findAllByPhone(String phone);
    // 이름으로 검색
    List<MemberEntity> findByNameContaining(String name);

    @Query("SELECT m FROM MemberEntity m WHERE m.phone LIKE %:phoneSuffix AND CONCAT(LPAD(CAST(FUNCTION('MONTH', m.birth) AS STRING), 2, '0'), LPAD(CAST(FUNCTION('DAY', m.birth) AS STRING), 2, '0')) = :birth")
    Optional<MemberEntity> findByPhoneEndingWithAndBirth(@Param("phoneSuffix") String phoneSuffix, @Param("birth") String birth);


    @Query("SELECT m FROM MemberEntity m WHERE m.stat = '장기미출석자'")
    List<MemberEntity> findLongAbsentMembers();

    @Query("SELECT m FROM MemberEntity m WHERE DATE(m.applicationDate) = CURRENT_DATE")
    List<MemberEntity> findTodayRegistrations();

    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE DATE(m.applicationDate) = CURRENT_DATE")
    Integer calculateTodayRevenue();

    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE DATE(m.applicationDate) = :date")
    Integer calculateDailyRegistrationRevenue(@Param("date") Date date);

    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE DATE(m.applicationDate) = CURRENT_DATE")
    Integer calculateTodayRegistrationRevenue();


    @Query("SELECT SUM(m.price) FROM MemberEntity m WHERE m.applicationDate >= :startDate AND m.applicationDate < :endDate")
    Integer sumPricesByApplicationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
   /* @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.remainingDays = m.remainingDays + ?2 WHERE m.id = ?1")
    void addRemainingDays(Long id, int dayTransfer);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.remainingDays = m.remainingDays - ?2 WHERE m.id = ?1")
    void subtractRemainingDays(Long id, int dayTransfer);*/

    List<MemberEntity> findAllByName(String name);

    List<MemberEntity> findAllById(Integer id);

    Optional<Object> findById(Long id);

    Optional<MemberEntity> findAllById(Long id);

    MemberEntity findByKakao(String kakao);

    List<MemberEntity> findByMemend(LocalDate memend);

    List<MemberEntity> findByRemainDays(long remainDays);

    void deleteById(Long id);

    List<MemberEntity> findByCoachNotAndStatus(String coach, String status, Sort sort);

    List<MemberEntity> findByCoachAndStatus(String coach, String status, Sort sort);
    Optional<MemberEntity> findByNameAndPhone(String name, String phone);

    MemberEntity findByNameAndBirth(String name, LocalDate birth);

    @Query("SELECT m FROM MemberEntity m WHERE m.name LIKE %:query% AND (m.status IS NULL OR m.status = 'approve')")
    List<MemberEntity> searchMembers(@Param("query") String query);

    // 전화번호와 상태로 회원 조회하는 메서드
    Optional<MemberEntity> findByPhoneAndStatus(String phone, String status);

    // 전화번호로 회원이 존재하는지 확인하는 메서드
    boolean existsByPhone(String phone);


    @Query("SELECT m FROM MemberEntity m WHERE m.remainDays > 0")
    List<MemberEntity> findMembersWithActiveMemberships();

}
