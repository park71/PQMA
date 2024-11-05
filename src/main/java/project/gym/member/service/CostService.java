package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gym.member.dto.CostDTO;
import project.gym.member.entity.CostEntity;
import project.gym.member.repository.CostRepository;

import java.util.List;

@Service
public class CostService {

    @Autowired
    private CostRepository costRepository;

    public void saveCost(CostDTO costDTO) {
        // Convert DTO to entity if needed
        CostEntity costEntity = new CostEntity();
        costEntity.setId(costDTO.getId());
        costEntity.setStatus(costDTO.getStatus());
        costEntity.setApplicant(costDTO.getApplicant());
        costEntity.setApplicantDate(costDTO.getApplicantDate());
        costEntity.setProduct(costDTO.getProduct());
        costEntity.setDetails(costDTO.getDetails());
        costEntity.setFixed(costDTO.getFixed());
        costEntity.setReceiveDate(costDTO.getReceiveDate());
        costEntity.setPay(costDTO.getPay());
        costEntity.setShop(costDTO.getShop());
        costEntity.setPhones(costDTO.getPhones());
        costEntity.setFinished(costDTO.getFinished());
        costEntity.setCost(costDTO.getCost());
        costEntity.setPayfull(costDTO.getPayfull());
        costEntity.setDelivery(costDTO.getDelivery());
        // Save entity to the database
        costRepository.save(costEntity);
    }

    // Method to get all costs
    public List<CostEntity> getAllCosts() {
        return costRepository.findAll(); // Fetch all cost records
    }
}
