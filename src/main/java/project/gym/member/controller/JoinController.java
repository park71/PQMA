package project.gym.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import project.gym.member.dto.JoinDTO;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.JoinService;

@Controller
@RequiredArgsConstructor
public class JoinController {

    @Autowired
    private JoinService joinService;

    @Autowired
    private UserRepository userRepository;

    public JoinController(JoinService joinService){
        this.joinService=joinService;
    }

    @GetMapping("/join")
    public String joinP(Model model){
        model.addAttribute("user", new JoinDTO());
        return "join";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/joinProc")
    public String joinProcess(JoinDTO joinDTO){

        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);

        return "redirect:/login";
    }

//    @GetMapping("/verifyEmail")
//    public String verifyEmail(@RequestParam("code") String code, @RequestParam("useryd") String useryd) {
//        UserEntity user = userRepository.findByUseryd(useryd);
//
//        if (user != null && code.equals(user.getVerificationCode())) {
//            user.setVerified(true); // 이메일 인증 완료
//            userRepository.save(user);
//            return "redirect:/join"; // 인증 후 로그인 페이지로 리다이렉트
//        }
//
//        return "redirect:/error"; // 인증 실패 시 에러 페이지로 리다이렉트
//    }

}
