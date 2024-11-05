package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.MembershipEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity, Long> {
    List<MembershipEntity> findByMemberId(Long id);

    Optional<MembershipEntity> findTopByOrderByIdDesc(); // 최신 레코드 조회

    List<MembershipEntity> findAllByName(String name);

    List<MembershipEntity> findByName(String name); // List로 반환

    List<MembershipEntity> findByPhone(String phone);

    MembershipEntity findOptionalByPhone(String phone);

    List<MembershipEntity> findByNameContaining(String name);

    List<MembershipEntity> findAllByMemberId(MemberEntity member);

    List<MembershipEntity> findAllByMember(MemberEntity member); // MemberEntity로 참조

}
