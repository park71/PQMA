package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.ConsultationEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<ConsultationEntity, Long> {

    List<ConsultationEntity> findBySangdamTime(LocalDateTime sangdamTime);

    boolean existsBySangdamTime(LocalDateTime sangdamTime);

}
