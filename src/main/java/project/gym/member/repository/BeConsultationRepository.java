package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.BeConsultationEntity;

@Repository
public interface BeConsultationRepository extends JpaRepository<BeConsultationEntity, Long> {


}
