package project.gym.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.gym.member.dto.BeConsultDTO;
import project.gym.member.entity.ConsultationEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.filter.JwtFilter;
import project.gym.member.filter.JwtUtil;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.MemberService;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;
    @GetMapping("/")
    public String homePage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loginUsername");

        String useryd = (String) session.getAttribute("loginUseryd");
        if (username != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginyd", useryd);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("consultationForm", new ConsultationEntity());
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("beconsultForm", new BeConsultDTO());
        }
        return "home";
    }

    // 남은 회원권이 있는 회원들에게 QR 코드 생성
    @GetMapping("/qr/members")
    public ResponseEntity<String> generateQRCodeForActiveMembers() {
        // QR 코드 생성 로직 호출
        memberService.generateQRCodeForMembersWithActiveMemberships();

        // 성공적으로 생성되었음을 반환
        return ResponseEntity.ok("QR 코드가 성공적으로 생성되었습니다.");
    }
    @GetMapping("/stat/reset")
    public ResponseEntity<String> resetStat(){
        memberService.updateLongTermAbsentMembersStatus();
        return  ResponseEntity.ok("성공적으로 리셋");
    }
}

