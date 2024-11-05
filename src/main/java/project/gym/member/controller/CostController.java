package project.gym.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import project.gym.member.dto.CostDTO;
import project.gym.member.entity.CostEntity;
import project.gym.member.service.CostService;

import java.util.List;

@Controller
public class CostController {

    @Autowired
    private CostService costService;

    // Method to display the cost page
    @GetMapping("/cost")
    public String showCostPage(Model model) {
        List<CostEntity> costList = costService.getAllCosts(); // Fetch all costs
        model.addAttribute("costList", costList); // Add the list to the model
        return "cost"; // Return the view name (cost.html)
    }
    @PostMapping("/coster")
    public ResponseEntity<String> saveCost(CostDTO costDTO) {
        costService.saveCost(costDTO);
        return ResponseEntity.ok("저장 완료"); // "Save completed" in Korean
    }


}
