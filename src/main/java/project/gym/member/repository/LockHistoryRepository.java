package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.LockHistoryEntity;

@Repository
public interface LockHistoryRepository extends JpaRepository<LockHistoryEntity, Long> {
}
