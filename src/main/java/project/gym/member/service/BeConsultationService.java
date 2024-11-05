package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gym.member.dto.BeConsultDTO;
import project.gym.member.entity.BeConsultationEntity;
import project.gym.member.repository.BeConsultationRepository;
import project.gym.member.repository.ConsultationRepository;

import java.util.List;

@Service
public class BeConsultationService {

    @Autowired
    private BeConsultationRepository beConsultationRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    public List<BeConsultationEntity> findALl(){
        return beConsultationRepository.findAll();
    }

    public void saveConsultation(BeConsultDTO beConsultDTO) {


        BeConsultationEntity beConsultationEntity = new BeConsultationEntity();
        beConsultationEntity.setUsername(beConsultDTO.getUsername());
        beConsultationEntity .setGender(beConsultDTO.getGender());
        beConsultationEntity .setMail(beConsultDTO.getMail());
        beConsultationEntity .setPhonenumber(beConsultDTO.getPhonenumber());
        beConsultationEntity .setExercise(beConsultDTO.getExercise());
        beConsultationEntity .setSangType(beConsultDTO.getSangType());
        beConsultationEntity .setSangTime(beConsultDTO.getSangTime());
        beConsultationEntity .setNote(beConsultDTO.getNote());

        beConsultationRepository.save(beConsultationEntity);
    }
    public List<BeConsultationEntity> finaAll(){
        return beConsultationRepository.findAll();
    }
}
