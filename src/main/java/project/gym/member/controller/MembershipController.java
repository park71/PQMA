package project.gym.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gym.member.dto.BeConsultDTO;
import project.gym.member.dto.MemberDTO;
import project.gym.member.entity.*;
import project.gym.member.repository.PTContractRepository;
import project.gym.member.repository.RestRepository;
import project.gym.member.repository.TrasnferRepository;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.MemberService;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/member")
@CrossOrigin(origins = "https://www.priqma.com")
public class MembershipController   {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  MemberService memberService;
    @Autowired
    private PTContractRepository ptContractRepository;
    @Autowired
    private TrasnferRepository trasnferRepository;
    @Autowired
    private RestRepository restRepository;

    @Autowired
    public MembershipController(MemberService memberService) {
        this.memberService = memberService;
    }
    // GET 요청: 계약서 폼을 표시하고 데이터를 모델에 추가합니다.
    @GetMapping("/apply")
    public String showApplyForm(Model model, HttpSession session) {
        String useryd = (String) session.getAttribute("loginUseryd");
        // 새로운 MemberEntity 객체를 생성하여 모델에 추가합니다.
        model.addAttribute("member", new MemberEntity());


        String username = (String) session.getAttribute("loginUsername");

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginUseryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("consultationForm", new ConsultationEntity());
                return "agreementuser"; // 폼을 표시할 Thymeleaf 템플릿
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("beconsultForm", new BeConsultDTO());
        }
        return "home";


    }



    @PostMapping("/apply") //회원이 신청한 회원권
    public String applyMembership(@ModelAttribute MemberEntity member, Model model) {
        // 전화번호로 기존 회원 찾기
        Optional<MemberEntity> existingMemberOpt = memberService.findByPhoneing(member.getPhone());
        if (existingMemberOpt.isPresent()) {
            // 기존 회원이 있을 경우, 기존 데이터를 덮어씌우고 상태를 'pending'으로 설정
            MemberEntity existingMember = existingMemberOpt.get();
            existingMember.setName(member.getName());
            existingMember.setPhone(member.getPhone());
            existingMember.setComein(member.getComein());
            existingMember.setPrice(member.getPrice());
            existingMember.setCredit(member.getCredit());
            existingMember.setPurpose(member.getPurpose());
            existingMember.setCoach(member.getCoach());
            existingMember.setGender(member.getGender());
            existingMember.setAddress(member.getAddress());
            existingMember.setBirth(member.getBirth());
            existingMember.setMemstart(member.getMemstart());
            existingMember.setMemend(member.getMemend());
            existingMember.setSignature(member.getSignature());

            existingMember.setKakao("만리" + existingMember.getName() + existingMember.getPhone().substring(existingMember.getPhone().length() - 4)); // 카카오 값 설정
            existingMember.setStatus("pending"); // 상태를 'pending'으로 변경
            // 변경된 기존 회원 저장
            memberService.save(existingMember);
        } else {
            // 신규 회원일 경우, 카카오 값 설정 및 상태를 'pending'으로 설정
            String kakao = "만리" + member.getName() + member.getPhone().substring(member.getPhone().length() - 4);
            member.setKakao(kakao);
            member.setStatus("pending");
            memberService.save(member);
        }
        return "redirect:/";
    }

    // 계약서 목록 페이지 (관리자가 확인)
    @GetMapping("/PT_apply") // PT 신청
    public String applyPersonal(Model model, HttpSession session){
        model.addAttribute("members", new PTContractEntity());
        String useryd = (String) session.getAttribute("loginUseryd");

        String username = (String) session.getAttribute("loginUsername");

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginUseryd",true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("consultationForm", new ConsultationEntity());
                return "PTagreementuser";
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("beconsultForm", new BeConsultDTO());
        }
        return "home";




    }
    @PostMapping("/PT_apply")
    public String applyPersonalTraining(@ModelAttribute PTContractEntity ptContract, Model model){
        ptContract.setStatus("pending");
        String phone =ptContract.getPhone();
        String kakao = "만리" + ptContract.getName() + phone.substring(phone.length() - 4);
        ptContract.setKakao(kakao);
        ptContractRepository.save(ptContract);

        return "redirect:/userdashboard";
    }
    // 양도 페이지로 이동하는 메서드
    @GetMapping("/trans")
    public String transfer(@RequestParam("phone") String phone, Model model, HttpSession session) {
        System.out.println("phone"+phone);
        String useryd = (String) session.getAttribute("loginUseryd");

        // 로그인한 사용자의 UserEntity 가져오기
        UserEntity user = userRepository.findByUseryd(useryd);

        // 전화번호로 회원을 조회합니다.
        Optional<MemberEntity> member = memberService.findByPhoneing(user.getPhnum());

        if (member.isPresent()) {
            // 조회된 회원 정보를 모델에 추가합니다.
            model.addAttribute("fromMember", member.get());

        } else {
            // 회원이 존재하지 않는 경우, 에러 페이지로 리다이렉트하거나 적절한 처리
            return "redirect:/errorPage";  // 에러 페이지로 리다이렉트
        }

        // 빈 TransferEntity 객체도 추가
        model.addAttribute("trans", new TransferEntity());

        // 세션에서 로그인한 사용자 정보를 가져옵니다.
        String username = (String) session.getAttribute("loginUsername");

        if (username != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginUseryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity users = userRepository.findByUseryd(useryd);
            if (users != null) {
                model.addAttribute("user", users);
                model.addAttribute("consultationForm", new ConsultationEntity());
                return "transagreement"; // 양도 페이지로 이동
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("beconsultForm", new BeConsultDTO());
        }
        return "home"; // 로그인하지 않은 경우 홈 페이지로 이동
    }


@GetMapping("/stop")
public String stopagree(Model model, @RequestParam String phone, HttpSession session) {
    System.out.println("요청된 전화번호: " + phone); // 요청된 전화번호 출력
    String useryd = (String) session.getAttribute("loginUseryd");

    // 로그인 여부 확인
    if (useryd == null) {
        return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉션
    }

    // 로그인한 사용자의 UserEntity 가져오기
    UserEntity user = userRepository.findByUseryd(useryd);

    if (user != null) {
        model.addAttribute("user", user);

        // 회원 전화번호로 회원 정보 조회
        MemberDTO memberDTO = memberService.findByPhoneNumber(user.getPhnum());
        RestEntity restEntity = new RestEntity();
        restEntity.setPhone(phone); // 요청된 전화번호 설정

        if (memberDTO != null) {
            // 단일 MemberDTO 객체로 처리
            System.out.println("회원 정보 찾았습니다.");
            model.addAttribute("membership", memberDTO);

            // 로그로 전화번호 출력
            log.info("회원 전화번호: " + memberDTO.getPhone());

            // DTO의 이름을 모델에 추가
            String actualName = memberDTO.getName();
            System.out.println("회원 이름: " + actualName);
            model.addAttribute("memberName", actualName);

        } else {
            // 회원 정보를 찾지 못한 경우
            model.addAttribute("membershipError", "회원권 정보를 찾을 수 없습니다.");
        }
        model.addAttribute("stop", new RestEntity()); // 신규 RestEntity 추가
    }

    // 세션에서 로그인된 사용자 이름을 가져옴
    String username = (String) session.getAttribute("loginUsername");

    if (username != null) {
        model.addAttribute("loggedIn", true);
        model.addAttribute("username", username);
        model.addAttribute("loginUseryd", true);

        // 사용자 정보를 조회하여 추가
        UserEntity users = userRepository.findByUseryd(useryd);
        if (users != null) {
            model.addAttribute("user", users);
            model.addAttribute("consultationForm", new ConsultationEntity());
            return "pauseFormuser"; // 환불 신청 페이지로 이동
        } else {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
        }
    } else {
        model.addAttribute("loggedIn", false);
        model.addAttribute("beconsultForm", new BeConsultDTO());
    }

    return "home"; // 기본 페이지로 이동
}



    @PostMapping("/stop")
    public String stopagreement(@ModelAttribute RestEntity rest, Model model){
        System.out.println("이름 뭐냐니까"+rest.getName());
        System.out.println("전화번호는?!!"+rest.getPhone());

        rest.setStatus("pending");
        restRepository.save(rest);
        return "redirect:/userdashboard";
    }

    ///////////////환불
    @GetMapping("/cashback")
    public String showCashbackForm(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        UserEntity user = userRepository.findByUseryd(useryd);
        if (user != null) {
            model.addAttribute("user", user);

            // 회원 이름을 사용하여 해당 회원의 정보를 조회
            MemberDTO memberDTO = memberService.findByPhoneNumber(user.getPhnum());
            model.addAttribute("member", memberDTO);

            try {
                System.out.println("가격: " + memberDTO.getPrice());
                System.out.println("결제방법: " + memberDTO.getCredit());

                // 환불 신청 폼에 사용할 객체 생성 및 초기화
                MemberEntity cashbackForm = new MemberEntity();
                cashbackForm.setName(memberDTO.getName());
                cashbackForm.setPhone(memberDTO.getPhone());
                cashbackForm.setBirth(memberDTO.getBirth());
                cashbackForm.setMembership(memberDTO.getMembership());
                cashbackForm.setMemstart(memberDTO.getMemstart());
                cashbackForm.setMemend(memberDTO.getMemend());
                cashbackForm.setLocker(memberDTO.getLocker());
                cashbackForm.setLockstart(memberDTO.getLockstart());
                cashbackForm.setLockend(memberDTO.getLockend());
                cashbackForm.setLocknum(memberDTO.getLocknum());
                cashbackForm.setShirt(memberDTO.getShirt());
                cashbackForm.setShirtstart(memberDTO.getShirtstart());
                cashbackForm.setShirtend(memberDTO.getShirtend());
                cashbackForm.setRemainDays(memberDTO.getRemainDays());
                cashbackForm.setPrice(memberDTO.getPrice());
                cashbackForm.setCredit(memberDTO.getCredit());
                model.addAttribute("member", cashbackForm);
            } finally {
                if (useryd != null) {
                    model.addAttribute("loggedIn", true);
                    model.addAttribute("username", username);
                    model.addAttribute("loginUseryd", true);

                    // 사용자 정보를 조회해서 추가
                    UserEntity users = userRepository.findByUseryd(useryd);
                    if (users != null) {
                        model.addAttribute("user", users);
                        model.addAttribute("consultationForm", new ConsultationEntity());
                    }
                    return "cashbackagreement";  // cashback.html로 이동
                } else {
                    model.addAttribute("error", "User not found");
                }
            }
        }
        model.addAttribute("loggedIn", false);
        model.addAttribute("beconsultForm", new BeConsultDTO());
        return "home";
    }











    @PostMapping("/refund")
    public String refundFormPage(@RequestParam("phone") String phone,
                                 @RequestParam("refundAmount") int refundAmount,
                                 @RequestParam("newCreditAccount") String newCreditAccount) {
        // Debugging logs
        System.out.println("회원 번호: " + phone);
        System.out.println("예상 환불 금액: " + refundAmount);
        System.out.println("새 계좌번호: " + newCreditAccount);

        // phone으로 회원 조회
        Optional<MemberEntity> optionalMember = memberService.findByPhoneing(phone);

        if (!optionalMember.isPresent()) {
            // 회원이 존재하지 않으면 에러 처리
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }

        MemberEntity member = optionalMember.get();

        String phones =member.getPhone();
        String kakao = "만리" + member.getName() + phones.substring(phones.length() - 4);
        member.setKakao(kakao);
        // Set refund amount, new credit account, and status
        member.setPrice(refundAmount);  // Set refund amount
        member.setCredit(newCreditAccount);  // Set new credit account
        member.setCoach("환불");
        member.setStatus("pending");  // Set status to pending

        // Save the updated member entity
        memberService.save(member);

        return "redirect:/userdashboard";
    }

    ///////////////////// PT환불
    @GetMapping("/ptcashback")
    public String ptCashback(@RequestParam("name") String name, Model model, HttpSession session) {
        // 단일 회원 정보를 가져오도록 수정
        String useryd = (String) session.getAttribute("loginUseryd");

        if (useryd == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 로그인한 사용자의 UserEntity 가져오기
        UserEntity user = userRepository.findByUseryd(useryd);
        if (user != null) {
            model.addAttribute("user", user);

            Optional<PTContractEntity> ptContract = memberService.findByPhonel(user.getPhnum());
            model.addAttribute("members", ptContract);

            String username = (String) session.getAttribute("loginUsername");

            if (username != null) {
                model.addAttribute("loggedIn", true);
                model.addAttribute("username", username);

                // 사용자 정보를 조회해서 추가
                UserEntity users = userRepository.findByUsername(username);
                if (users != null) {
                    model.addAttribute("user", users);
                    model.addAttribute("consultationForm", new ConsultationEntity());
                    return "PTcashbackagreement"; // PTcashbackagreement.html로 이동
                } else {
                    model.addAttribute("error", "User not found");
                }
            } else {
                model.addAttribute("loggedIn", false);
                model.addAttribute("beconsultForm", new BeConsultDTO());
            }
        }

        return "home"; // 사용자 정보가 없거나 로그인하지 않은 경우 홈 페이지로 이동
    }

    @PostMapping("/ptcashback")
    public String ptrefund(Model model, @RequestParam("phone") String phone,
                           @RequestParam("refundAmount") int refundAmount,
                           @RequestParam("newCreditAccount") String newCreditAccount){


        Optional<PTContractEntity> ptContract = memberService.findByPhonel(phone);

        PTContractEntity ptContract1 = ptContract.get();

        String phones =ptContract1.getPhone();
        String kakao = "만리" + ptContract1.getName() + phones.substring(phones.length() - 4);
        ptContract1.setKakao(kakao);
        ptContract1.setPrice(String.valueOf(refundAmount));
        ptContract1.setCoach("환불");
        ptContract1.setCredit(newCreditAccount);
        ptContract1.setStatus("pending");

        ptContractRepository.save(ptContract1);




        return "redirect:/userdashboard";
    }



}
