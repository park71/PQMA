package project.gym.member.controller;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gym.member.dto.MemberDTO;
import project.gym.member.dto.TransferDTO;
import project.gym.member.entity.*;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.PTContractRepository;
import project.gym.member.service.MemberService;
import project.gym.member.service.QRService;
import project.gym.member.service.RestService;
import project.gym.member.service.TransferService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = "https://www.priqma.com")
public class AdminapproveController {


    @Autowired
    private TransferService transferService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PTContractRepository ptContractRepository; // PTContractEntity 저장을 위한 Repository 주입
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/membershipApplication")
    public String viewApplications(Model model) {
        // 조건에 맞는 회원 목록 조회
        List<MemberEntity> applications = memberService.findNonRefundPendingApplications(Sort.by(Sort.Direction.DESC, "applicationDate"));

        // 모델에 회원 목록 추가
        model.addAttribute("applications", applications);

        return "membershipApplication"; // 관리자가 확인할 Thymeleaf 템플릿
    }


    @PostMapping("/membership-applications/{id}/approve")
    public String approveMember(@PathVariable Integer id) {
        // ID로 MemberEntity 찾기
        MemberEntity member = memberService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 상태를 'approved'로 변경
        member.setStatus("approved");

        if(!"일일입장".equals(member.getMembership())){
            // QR 코드 생성
            QRService qrCodeService = new QRService();
            LocalDate birthDateLocal = member.getBirth(); // assuming this is LocalDate
            String birthDate = birthDateLocal.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 형식 변경
            String qrCodeText = "PQMA 출입증" +
                    "\n이름: " + member.getName() +
                    "\n전화번호: " + member.getPhone() +
                    "\n생년월일: " + birthDate; // 수정된 생년월일 사용
            // QR 코드를 파일로 저장할 경로 설정
            String qrCodePath = "src/main/resources/static/qrcode/" + member.getPhone() + ".png"; // 전화번호를 사용하여 파일 이름 설정

            try {
                // QR 코드를 파일로 저장
                qrCodeService.generateQRCodeImage(qrCodeText, 300, 300, qrCodePath);
                member.setQrCodePath(qrCodePath); // QR 코드 경로를 member에 설정

                // 다시 멤버 정보 저장
                memberRepository.save(member);
            } catch (WriterException | IOException e) {
                e.printStackTrace(); // 에러 처리
            }
        }
        // 전화번호로 기존 회원 찾기
        Optional<MemberEntity> existingMemberOpt = memberService.findByPhoneing(member.getPhone());
        if (existingMemberOpt.isPresent()) {
            MemberEntity existingMember = existingMemberOpt.get();

            // 필요한 정보 덮어쓰기 (기존 정보는 유지할 수 있음)
            existingMember.setName(member.getName());
            existingMember.setMemstart(member.getMemstart());
            existingMember.setMemend(member.getMemend());
            existingMember.setPrice(member.getPrice());
            existingMember.setCredit(member.getCredit());
            existingMember.setCoach(member.getCoach());
            existingMember.setGender(member.getGender());
            existingMember.setAddress(member.getAddress());
            existingMember.setBirth(member.getBirth());
            existingMember.setKakao(member.getKakao());
            existingMember.setComein(member.getComein());
            existingMember.setPurpose(member.getPurpose());
            existingMember.setMembership(member.getMembership());
            existingMember.setLocker(member.getLocker());
            existingMember.setLocknum(member.getLocknum());
            existingMember.setLockstart(member.getLockstart());
            existingMember.setLockend(member.getLockend());
            existingMember.setShirt(member.getShirt());
            existingMember.setShirtstart(member.getShirtstart());
            existingMember.setShirtend(member.getShirtend());
            existingMember.setSignature(member.getSignature());

            // 업데이트한 기존 회원 정보 저장
            memberService.save(existingMember);
        }else {
            // 새로운 회원 생성
            MemberEntity newMember = new MemberEntity();

            // 새로운 회원 정보 설정
            newMember.setName(member.getName());
            newMember.setPhone(member.getPhone());
            newMember.setMemstart(member.getMemstart());
            newMember.setMemend(member.getMemend());
            newMember.setPrice(member.getPrice());
            newMember.setCredit(member.getCredit());
            newMember.setCoach(member.getCoach());
            newMember.setGender(member.getGender());
            newMember.setAddress(member.getAddress());
            newMember.setBirth(member.getBirth());
            newMember.setKakao(member.getKakao());
            newMember.setComein(member.getComein());
            newMember.setPurpose(member.getPurpose());
            newMember.setMembership(member.getMembership());
            newMember.setLocker(member.getLocker());
            newMember.setLocknum(member.getLocknum());
            newMember.setLockstart(member.getLockstart());
            newMember.setLockend(member.getLockend());
            newMember.setShirt(member.getShirt());
            newMember.setShirtstart(member.getShirtstart());
            newMember.setShirtend(member.getShirtend());
            newMember.setSignature(member.getSignature());
            // 새로운 회원 정보 저장
            memberService.save(newMember);
        }

        // 새로운 MembershipEntity 생성 및 연결
        MembershipEntity membership = new MembershipEntity();
        membership.setName(member.getName());
        membership.setPhone(member.getPhone());
        membership.setMemstart(member.getMemstart());
        membership.setMemend(member.getMemend());
        membership.setPrice(member.getPrice());
        membership.setCredit(member.getCredit());
        membership.setCoach(member.getCoach());
        membership.setGender(member.getGender());
        membership.setAddress(member.getAddress());
        membership.setBirth(member.getBirth());
        membership.setKakao(member.getKakao());
        membership.setComein(member.getComein());
        membership.setPurpose(member.getPurpose());
        membership.setMembership(member.getMembership());
        membership.setLocker(member.getLocker());
        membership.setLocknum(member.getLocknum());
        membership.setLockstart(member.getLockstart());
        membership.setLockend(member.getLockend());
        membership.setShirt(member.getShirt());
        membership.setShirtstart(member.getShirtstart());
        membership.setShirtend(member.getShirtend());



        membership.setMember(member);

        // 새 멤버십을 멤버의 계약 목록에 추가
        member.getContracts().add(membership);

        // MembershipEntity를 저장
        memberService.saveMembership(membership);
        // 새로운 MemberEntity도 저장
        memberService.save(member);

        // 신청 목록 페이지로 리다이렉트
        return "redirect:/adminPage";
    }





    @PostMapping("/membership-applications/{id}/reject")
    public String rejectMember(@PathVariable Integer id) {
        // ID로 MemberEntity 찾기
        MemberEntity member = memberService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 멤버 상태를 'rejected'로 설정
        member.setStatus("rejected");
        memberService.save(member); // 상태 업데이트 저장

        // 회원 정보 삭제
        memberService.delete(member); // 해당 회원 삭제

        return "redirect:/adminPage"; // 신청 목록 페이지로 리다이렉트
    }


    @GetMapping("/PTMembershipApplication") // 승인,거절페이지
    public String viewPTApplication(Model model){
        List<PTContractEntity> applications = memberService.findPendingApplication(Sort.by(Sort.Direction.DESC, "applicationDate"));
        model.addAttribute("applications", applications);
        return "PTMembershipApplication"; // 관리자가 확인할 Thymeleaf 템플릿
    }

    @PostMapping("/membership-application/{id}/approve")
    public String approveMembers(@PathVariable Long id, Model model) {
        PTContractEntity members = memberService.findByIds(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        members.setStatus("approved");  // 승인 상태로 설정
        memberService.saves(members);

        return "redirect:/admin/PTMembershipApplication"; // 신청 목록 페이지로 리다이렉트
    }

    @PostMapping("/membership-application/{id}/reject")
    public String rejectMembers(@PathVariable Long id) {
        PTContractEntity members = memberService.findByIds(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        members.setStatus("rejected");  // 거절 상태로 설정
        memberService.saves(members);

        return "redirect:/admin/PTMembershipApplication"; // 신청 목록 페이지로 리다이렉트
    }
    /////////////////////////////////////////////////////////////////////////양도

    @GetMapping("/transApplication") // 승인,거절페이지
    public String transferApplication(Model model){

        List<TransferEntity> transApplication = memberService.findPendingApplicationin(Sort.by(Sort.Direction.DESC, "applicationDate"));
        // Debugging line
        transApplication.forEach(trans -> System.out.println(trans.getFromMember()));
        model.addAttribute("transApplication", transApplication);
        return "transApplication"; // 관리자가 확인할 Thymeleaf 템플릿
    }
    @PostMapping("/membershipa-application/{id}/approve")
    public String transfering(@PathVariable Long id, Model model, TransferDTO transferDTO) {
        System.out.println("FromMemberId: " + transferDTO.getFromMemberId());
        System.out.println("ToMemberId: " + transferDTO.getToMemberId());
        System.out.println("daysToTransfer:"+transferDTO.getDaysToTransfer());

        TransferEntity membar = memberService.findByIa(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        membar.setStatus("approved");  // 승인 상태로 설정
        membar.setPrice("55000");
        transferService.transferMemberships(transferDTO.getFromMemberId(), transferDTO.getToMemberId(), transferDTO.getDaysToTransfer());
        memberService.saver(membar);

        return "redirect:/admin/transApplication"; // 신청 목록 페이지로 리다이렉트
    }


    @PostMapping("/membershipa-application/{id}/reject")
    public String transreject(@PathVariable Long id, Model model) {
        TransferEntity membar = memberService.findByIa(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        membar.setStatus("rejected");  // 승인 상태로 설정
        memberService.saver(membar);

        return "redirect:/admin/transApplication"; // 신청 목록 페이지로 리다이렉트
    }




    ///////////////////////////////////////////////////////////////휴회
    @GetMapping("/restApplication") // 승인,거절페이지
    public String restApplication(Model model){
        List<RestEntity> restApplication = memberService.findPendingApplicationing(Sort.by(Sort.Direction.DESC, "applicationDate"));
        model.addAttribute("restApplication", restApplication);
        return "restApplication"; // 관리자가 확인할 Thymeleaf 템플릿
    }
    @PostMapping("/memberships-application/{id}/approve")
    public String restApplications(@PathVariable Long id, @RequestParam String phone, Model model, @ModelAttribute RestEntity restEntity) {
        try {
            // ID를 사용하여 해당 회원 정보를 조회
            RestEntity membera = memberService.findByIam(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

            System.out.println("id 값: " + id);
            System.out.println("회원 이름: " + membera.getName());
            System.out.println("전화번호:"+membera.getPhone());
            System.out.println("딜레이 회원권: "+membera.getDelayDays());
            System.out.println("딜레이 운동복:"+membera.getDelayDaysForShirt());
            System.out.println("RestEntity delayDays: " + restEntity.getDelayDays());

// 회원권 종료일 변경
            memberService.updateMembershipEndDate(membera.getPhone(), membera.getDelayDays());

// 락카 종료일 변경
            memberService.updateLockerEndDate(membera.getPhone(), membera.getDelayDaysForLocker());

// 운동복 종료일 변경
            memberService.updateShirtEndDate(membera.getPhone(), membera.getDelayDaysForShirt());

            System.out.println("여긴가는거야?");
            // 상태 업데이트
            membera.setStatus("approved");

            // 휴지 신청 저장
            RestService.savePauseRequest(membera);

            return "redirect:/admin/restApplication";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "회원 정보를 찾을 수 없습니다: " + ex.getMessage());
            return "error";  // error.html 페이지로 리디렉션
        } catch (Exception ex) {
            model.addAttribute("error", "알 수 없는 오류가 발생했습니다: " + ex.getMessage());
            return "error";
        }
    }


    @PostMapping("/memberships-application/{id}/reject")
    public String restrejectMembers(@PathVariable Long id, Model model) {
        RestEntity membering = memberService.findByIam(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        membering.setStatus("rejected");  // 거절
        memberService.saveing(membering);

        return "redirect:/admin/restApplication"; // 신청 목록 페이지로 리다이렉트
    }
    ///////////////////////////////////////////////////환불

    @GetMapping("/cashbackApplication")
    public String refundPage(Model model) {
        List<MemberEntity> approvedMembers = memberService.findPendingRefundApplications(Sort.by(Sort.Direction.DESC, "applicationDate"));
        model.addAttribute("applications", approvedMembers);

        return "cashbackApplication"; // 관리자가 확인할 Thymeleaf 템플릿
    }

    @PostMapping("/memberships-apply/{id}/approve")
    public String refundcashback(@PathVariable Integer id, @ModelAttribute MemberDTO memberDTO, @RequestParam("refundAmount") int refundAmount, @RequestParam("newCreditAccount") String newCreditAccount) {
// 회원 정보 조회
        System.out.println("hibrother");
        // 회원 정보 조회
        Optional<MemberEntity> optionalMember = memberService.findById(memberDTO.getId());

        // 회원이 존재하는지 확인
        if (optionalMember.isPresent()) {
            MemberEntity member = (MemberEntity) optionalMember.get();


            // 값 초기화 (null로 변경)
            member.setMembership(null);
            member.setMemstart(null);
            member.setMemend(null);
            member.setLocker(null);
            member.setLockstart(null);
            member.setLockend(null);
            member.setLocknum(null);
            member.setShirt(null);
            member.setShirtstart(null);
            member.setShirtend(null);

            // 기존에 연결된 MembershipEntity를 가져오기
            List<MembershipEntity> memberships = member.getMemberships();

            // 모든 MembershipEntity의 값을 null로 초기화
            for (MembershipEntity membership : memberships) {
                membership.setMembership(null);
                membership.setMemstart(null);
                membership.setMemend(null);
                membership.setLocker(null);
                membership.setLockstart(null);
                membership.setLocknum(null);
                membership.setLockend(null);
                membership.setShirt(null);
                membership.setShirtstart(null);
                membership.setShirtend(null);

                // 예상 환불 금액 및 새 계좌번호 설정
                membership.setPrice(refundAmount);
                membership.setCredit(newCreditAccount);
                membership.setCoach("환불처리완료");
            }



            // 예상 환불 금액 및 새 계좌번호 설정
            member.setCoach("환불처리완료");
            member.setPrice(refundAmount);
            member.setCredit(newCreditAccount);


            // 상태를 'approved'로 변경
            member.setStatus("approved");
            // 초기화된 정보 저장
            memberService.save(member);

        }
        return "redirect:/admin/cashbackApplication";
    }
    @PostMapping("/memberships-apply/{id}/reject")
    public String refundrejectPage(@PathVariable Integer id) {
        MemberEntity member = memberService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        member.setStatus("rejected");  // 거절 상태로 설정
        memberService.save(member);

        return "redirect:/admin/membership-applications"; // 신청 목록 페이지로 리다이렉트
    }




    ////////////////////////////////////////// PT 환불

    @GetMapping("/ptcashbackApplication")
    public String ptcashback(Model model){
        List<PTContractEntity> applications = memberService.findNonRefundPendingApplication(Sort.by(Sort.Direction.DESC, "applicationDate"));
        model.addAttribute("applications", applications);

        return "PTcashbackApplication";
    }
    @PostMapping("/member-application/{id}/approve")
    public String approveptcashback(Model model,
                                    @PathVariable Integer id,
                                    @RequestParam("refundAmount") int refundAmount,
                                    @RequestParam("newCreditAccount") String newCreditAccount){

        Optional<PTContractEntity> optionalMember = memberService.findByIds(Long.valueOf(id));

        // 회원이 존재하는지 확인
        if (optionalMember.isPresent()) {
            PTContractEntity member = optionalMember.get();

            // 값 초기화 (null로 변경)
            member.setPtmembership(null);
            member.setPtstart(null);
            member.setCount(null);

            member.setCredit(newCreditAccount);
            member.setPrice(String.valueOf(refundAmount));
            member.setCoach("환불처리완료");
            member.setStatus("approved");

            memberService.saves(member);



        }
        return "redirect:/admin/PTcashbackApplication";
    }
    @PostMapping("/member-application/{id}/reject")
    public String rejectMembersf(@PathVariable Long id) {
        PTContractEntity members = memberService.findByIds(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        members.setStatus("rejected");  // 거절 상태로 설정
        memberService.saves(members);

        return "redirect:/admin/PTcashbackApplication"; // 신청 목록 페이지로 리다이렉트
    }


}
