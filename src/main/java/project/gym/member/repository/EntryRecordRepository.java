package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.EntryRecordEntity;
import project.gym.member.entity.MemberEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntryRecordRepository extends JpaRepository<EntryRecordEntity, Long> {
    List<EntryRecordEntity> findByMemberId(Long memberId);


    @Query("SELECT COUNT(e) > 0 FROM EntryRecordEntity e WHERE e.member = :member AND FUNCTION('DATE', e.entryTime) = :date")
    boolean existsByMemberAndEntryDate(@Param("member") MemberEntity member, @Param("date") LocalDate date);
    List<EntryRecordEntity> findByMemberName(String name);

    List<EntryRecordEntity> findByEntryTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByMemberAndEntryTimeAfter(MemberEntity member, LocalDateTime oneWeekAgo);

    // 특정 회원의 가장 최근 출입 기록을 반환하는 메서드
    EntryRecordEntity findTopByMemberOrderByEntryTimeDesc(MemberEntity member);

    @Query("SELECT e FROM EntryRecordEntity e WHERE e.entryTime >= :startOfDay AND e.entryTime < :endOfDay")
    List<EntryRecordEntity> findTodayAttendance(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

        @Query("SELECT SUM(m.price) FROM EntryRecordEntity e JOIN e.member m WHERE e.entryTime BETWEEN :startDate AND :endDate")
        Integer sumPricesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//    // 오늘의 출입 기록을 가져오는 메서드
//    @Query("SELECT e FROM EntryRecordEntity e WHERE e.entryTime >= :start AND e.entryTime < :end")
//    EntryRecordEntity findTodayEntries(Integer id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}