package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.DecrementRecord;

import java.util.List;

@Repository
public interface DecrementRecordRepository extends JpaRepository<DecrementRecord, Long> {
    List<DecrementRecord> findByPtContractId(Long ptContractId);
}