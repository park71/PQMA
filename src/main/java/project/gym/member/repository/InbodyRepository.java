package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.InbodyEntity;

import java.util.List;

@Repository
public interface InbodyRepository extends JpaRepository<InbodyEntity, Long> {


//    // 특정 회원의 마지막 회차 조회
//    @Query("SELECT MAX(i.counter) FROM InbodyEntity i WHERE i.member.id = :memberId")
//    Optional<Integer> findLastCounterByMemberId(@Param("memberId") Long memberId);
//
//    // 특정 회원의 인바디 기록을 날짜 내림차순으로 조회
//    List<InbodyEntity> findByMemberIdOrderByRecordDateDesc(Long memberId);

    @Query("SELECT i FROM InbodyEntity i WHERE i.name = :name")
    List<InbodyEntity> findByName(@Param("name") String name);

}
