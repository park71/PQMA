package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.LockerEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<LockerEntity, Long> {
    List<LockerEntity> findByLocknum(Integer locknum);

    Optional<LockerEntity> findByName(String name);

    // MemberEntity의 ID를 통해 LockerEntity 조회
    LockerEntity findByMemnum(String memnum);
}
