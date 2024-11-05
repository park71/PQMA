package project.gym.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.gym.member.dto.EntryRecordDTO;
import project.gym.member.dto.LockerDTO;
import project.gym.member.dto.MemberDTO;
import project.gym.member.dto.MembershipDTO;
import project.gym.member.entity.PTContractEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.EntryRecordService;
import project.gym.member.service.LockerService;
import project.gym.member.service.MemberService;

import java.util.List;

@Controller
public class UserListController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LockerService lockerService;
    @Autowired
    private EntryRecordService entryRecordService;

        @GetMapping("/userdashboard")
        public String getMemberDetail(HttpSession session, Model model) {
            // 로그인한 사용자의 UserEntity 가져오기
            String username = (String) session.getAttribute("loginUsername");
            String useryd = (String) session.getAttribute("loginUseryd");
            System.out.println("User: " + useryd);


            if (useryd == null) {
                return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
            }

            // 로그인한 사용자의 UserEntity 가져오기
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);

                // UserEntity의 전화번호로 MemberDTO 반환
                MemberDTO memberDTO = memberService.findByPhoneNumber(user.getPhnum());
                System.out.println("Member DTO: " + memberDTO);
                if (memberDTO != null) {
                    model.addAttribute("member", memberDTO);
                    // 멤버의 모든 계약 정보를 가져옴 (회원권)
                    List<MembershipDTO> memberships = memberDTO.getMemberships();
                    if (memberships != null && !memberships.isEmpty()) {
                        model.addAttribute("memberships", memberships);
                    } else {
                        model.addAttribute("membershipError", "회원권이 아직 없습니다.");
                    }
                    // 멤버 이름을 사용하여 출입 기록을 가져옴
                    List<EntryRecordDTO> entryRecordDTOs = entryRecordService.findByMemberId(Long.valueOf(memberDTO.getId()));
                    if (entryRecordDTOs != null && !entryRecordDTOs.isEmpty()) {
                        System.out.println("entry값있다");
                        System.out.println(entryRecordDTOs);
                        model.addAttribute("entryRecords", entryRecordDTOs);
                    } else {
                        model.addAttribute("entryRecordError", "출입 기록이 없습니다.");
                    }
                    // 멤버 이름을 사용하여 락커 정보를 가져옴
                    LockerDTO lockerDTO = lockerService.findByMemberId(String.valueOf(memberDTO.getId()));
                    if (lockerDTO != null) {
                        model.addAttribute("lockers", lockerDTO);
                    } else {
                        model.addAttribute("lockerError", "락커 정보를 찾을 수 없습니다.");
                    }
                } else {
                    model.addAttribute("membershipError", "회원권이 아직 없습니다.");
                }
                // 멤버 이름을 사용하여 PT 계약 정보를 가져옴
                // 여기서는 memberDTO가 null인 경우를 대비해 UserEntity의 정보를 사용하여 조회
                String memberName = memberDTO != null ? memberDTO.getName() : user.getUsername(); // memberDTO가 null일 경우 user.getName() 사용
                List<PTContractEntity> ptContracts = memberService.findByMemberName(memberName);
                if (ptContracts != null && !ptContracts.isEmpty()) {
                    model.addAttribute("ptContracts", ptContracts);
                } else {
                    model.addAttribute("ptContractError", "PT 계약 정보를 찾을 수 없습니다.");
                }
                System.out.println(ptContracts);

            } else {
                model.addAttribute("membershipError", "해당 전화번호로 등록된 회원권이 없습니다."); // 회원권이 없음을 알림
            }

            // 기본값으로 loggedIn 변수를 false로 설정
            model.addAttribute("loggedIn", false);
            model.addAttribute("loginUseryd", false);
            if (user != null) {
                model.addAttribute("loginUseryd", true);
                model.addAttribute("loggedIn", true);
                model.addAttribute("username", username);
                if (user != null) {
                    model.addAttribute("user", user);
                } else {
                    model.addAttribute("error", "User not found");
                }
            }
            return "userdashboard";
        }


    @GetMapping("/member/usepop.html")
    public String showPopupPage() {
        return "usepop"; // usepop.html이 templates에 있을 경우
    }
    @GetMapping("/member/gaepop.html") // URL을 맞춰줍니다
    public String showGaepopPage() {
        return "gaepop"; // gaepop.html이 templates에 있을 경우
    }
    @GetMapping("/member/hwanpop.html")
    public String showPwopupPage() {
        return "hwanpop"; // usepop.html이 templates에 있을 경우
    }
    @GetMapping("/member/yangpop.html")
    public String showsPopupPage() {
        return "yangpop"; // usepop.html이 templates에 있을 경우
    }
    @GetMapping("/member/pmpop.html")
    public String showsPopupPages() {
        return "pmpop"; // usepop.html이 templates에 있을 경우
    }
}
