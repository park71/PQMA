package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gym.member.entity.ConsultationEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.ConsultationRepository;
import project.gym.member.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private  ConsultationRepository consultationRepository;
    @Autowired
    private UserRepository userRepository;


    // 상담 예약 처리 메서드
    @Transactional
    public String scheduleConsultation(UserEntity user, String sangdamType, LocalDateTime sangdamTime, String notes) {
// 겹치는 상담이 있는지 확인
        if (consultationRepository.existsBySangdamTime(sangdamTime)) {
            return "다른 시간대를 선택해주세요.";
        }
// 상담 저장
        ConsultationEntity consultation = new ConsultationEntity();

        consultation.setUser(user);
        consultation.setUsername(user.getUsername());
        consultation.setEmail(user.getEmail());
        consultation.setPhnum(user.getPhnum());
        consultation.setSangdamType(sangdamType);
        consultation.setSangdamTime(sangdamTime);
        consultation.setNotes(notes);

        consultationRepository.save(consultation);

        return "상담 예약이 완료되었습니다.";
    }
    // 같은 시간대에 상담이 있는지 확인하는 메서드
    public boolean checkDuplicateConsultation(LocalDateTime sangdamTime) {
        return consultationRepository.existsBySangdamTime(sangdamTime);
    }


   /* public ConsultationDTO convertToDTO(ConsultationEntity consultationEntity) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setId(consultationEntity.getId());
        dto.setUser(consultationEntity.getUser());
        dto.setSangdamType(consultationEntity.getSangdamType());
        dto.setSangdamTime(consultationEntity.getSangdamTime());
        dto.setNotes(consultationEntity.getNotes());
        return dto;
    }

    public ConsultationEntity convertToEntity(ConsultationDTO consultationDTO, UserEntity user) {
        ConsultationEntity consultationEntity = new ConsultationEntity();
        consultationEntity.setUser(consultationDTO.getUser());
        consultationEntity.setSangdamType(consultationDTO.getSangdamType());
        consultationEntity.setSangdamTime(consultationDTO.getSangdamTime());
        consultationEntity.setNotes(consultationDTO.getNotes());
        return consultationEntity;
    }*/
   public  List<ConsultationEntity> findAll() {
       return consultationRepository.findAll();
   }


}
