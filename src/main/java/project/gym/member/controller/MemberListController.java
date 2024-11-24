package project.gym.member.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.gym.member.dto.EntryRecordDTO;
import project.gym.member.dto.LockerDTO;
import project.gym.member.dto.MemberDTO;
import project.gym.member.dto.MembershipDTO;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.RestEntity;
import project.gym.member.repository.MembershipRepository;
import project.gym.member.service.EntryRecordService;
import project.gym.member.service.LockerService;
import project.gym.member.service.MemberService;
import project.gym.member.service.RestService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/member")
@CrossOrigin(origins = "https://www.priqma.com")
public class MemberListController {

    @Autowired
    private RestService restService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private EntryRecordService entryRecordService;

    @Autowired
    private LockerService lockerService;
    @Autowired
    private MembershipRepository membershipRepository;





   @GetMapping("/{id}")
   public String getMemberDetail(@PathVariable Long id, Model model) {
       MemberDTO memberDTO = memberService.findByIding(id);
       if (memberDTO != null) {
           model.addAttribute("member", memberDTO);

           // 멤버의 모든 계약 정보를 가져옴
           List<MembershipDTO> memberships = memberService.findMembershipsByMemberId(id);
           model.addAttribute("memberships", memberships);

           // 해당 ID로 출입 기록을 조회
           List<EntryRecordDTO> entryRecordDTOs = entryRecordService.findByMemberId(id);
           if (!entryRecordDTOs.isEmpty()) {
               model.addAttribute("entryRecords", entryRecordDTOs);
           } else {
               model.addAttribute("entryRecordError", "출입 기록을 찾을 수 없습니다.");
           }

           // 해당 ID로 락커 정보를 조회
           LockerDTO lockerDTO = lockerService.findByMemberId(String.valueOf(id));
           if (lockerDTO != null) {
               model.addAttribute("lockers", lockerDTO);
               log.info("락커 정보: " + lockerDTO);
           } else {
               log.warn("No locker information found for member: " + memberDTO.getName());
               model.addAttribute("lockerError", "락커 정보를 찾을 수 없습니다.");
           }
       } else {
           model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
       }
        log.info("멤버아이디값", id);
       return "dashboard";
   }

    // 회원 수정 페이지로 이동
    @GetMapping("/edit/{id}")
    public String editMember(@PathVariable("id") Long id, Model model) {
        MemberEntity member = memberService.getMemberByMemberId(id);
// Controller에서 LocalDate 값을 포맷하여 넘겨줍니다.
        // 날짜 필드가 null인지 확인하고 null이면 빈 문자열을 설정
        model.addAttribute("memstart", member.getMemstart() != null ? member.getMemstart().toString() : "");
        model.addAttribute("memend", member.getMemend() != null ? member.getMemend().toString() : "");
        model.addAttribute("lockstart", member.getLockstart() != null ? member.getLockstart().toString() : "");
        model.addAttribute("lockend", member.getLockend() != null ? member.getLockend().toString() : "");
        model.addAttribute("shirtstart", member.getShirtstart() != null ? member.getShirtstart().toString() : "");
        model.addAttribute("shirtend", member.getShirtend() != null ? member.getShirtend().toString() : "");

        model.addAttribute("member", member);




        return "editMember";  // editMember.html 템플릿으로 이동
    }
//    @CrossOrigin(origins = "https://www.priqma.com")
//    @PostMapping("/edit/{id}")
//    public String updateMember(
//            @PathVariable("id") Long id,
//            @Valid @ModelAttribute MemberEntity member,
//            @RequestPart(value = "profileImage") MultipartFile profileFile,
//            BindingResult result) throws Exception {
//
//        if (result.hasErrors()) {
//            return "error";  // 오류가 있을 경우 오류 화면으로
//        }
//
//        System.out.println("업데이트 시작");
//        // 프로필 이미지가 업로드 되면 처리
//        if (!profileFile.isEmpty()) {
//            String fileName = memberService.handleFileUpload(profileFile);  // 파일 업로드 처리
//            member.setProfile(fileName); // 파일 이름을 profile에 설정
//        }
//
//
//
//        System.out.println("정보업데이트");
//        // 회원 정보 업데이트
//        memberService.updateMember(id, member);
//
//
//        return "redirect:/memberList";
//    }

        @PostMapping("/edit/{id}")
        public String updateMember(@PathVariable("id") Long id, MemberEntity member, Model model) {
            memberService.updateMember(id, member);
            return "redirect:/memberList";  // 수정 후 회원 목록 페이지로 리다이렉트
        }
    @PostMapping("/edit/image/{id}")
    public String updateMemberProfileImage(
            @PathVariable("id") Long id,
            @RequestParam("profileImage") MultipartFile profileFile) throws Exception {

        if (!profileFile.isEmpty()) {
            System.out.println("프로필 이미지 업데이트 시작");
            String fileName = memberService.handleFileUpload(profileFile);
            memberService.updateMemberProfileImage(id, fileName);
        }

        return "redirect:/memberList";
    }

//
//    // 인바디 기록 저장
//    @PostMapping("/inbody/{memberId}")
//    public ResponseEntity<String> addInbodyRecord(@PathVariable Long memberId) {
//        memberService.addInbodyRecord(memberId);
//        return ResponseEntity.ok("인바디 기록이 저장되었습니다.");
//    }
//
//    // 인바디 기록 조회
//    @GetMapping("/inbody/history/{memberId}")
//    public ResponseEntity<List<InbodyEntity>> getInbodyHistory(@PathVariable Long memberId) {
//        List<InbodyEntity> inbodyRecords = memberService .getInbodyRecordsByMemberId(memberId);
//        return ResponseEntity.ok(inbodyRecords);
//    }

    // 회원 삭제
    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") Long id,Model model) {
        memberService.deleteMember(id);
        return "redirect:/memberList";  // 삭제 후 회원 목록 페이지로 리다이렉트
    }

    @PostMapping("/memo")
    public String updateMember(
            @RequestParam("memberId") Long memberId,
            @RequestParam("content") String content,
            RedirectAttributes redirectAttributes, Model model) {
       log.info("아이디값",memberId);

        // 서비스 메서드를 호출하여 데이터 저장
        memberService.updateMemberContent(memberId, content);

        redirectAttributes.addFlashAttribute("message", "메모가 저장되었습니다.");
        return "redirect:/member/" + memberId; // 저장 후 회원 상세 페이지로 리다이렉트
    }

 @GetMapping("/memberList/{id}") //멤버리스트에서 해당회원 휴회페이지로 이동
 public String showPauseForm(@PathVariable("id") Integer id, Model model) {
     log.info(String.valueOf(id));
     try {
         MemberDTO memberDTO = memberService.findByNames(id);
         model.addAttribute("member",memberDTO);
         model.addAttribute("memberName", id);
         RestEntity restEntity = new RestEntity();
         restEntity.setId(Long.valueOf(id));
         model.addAttribute("pauseForm", restEntity);

         return "pauseForm";
     } catch (EntityNotFoundException ex) {
         model.addAttribute("error", ex.getMessage());
         return "error";
     }
 }

    @PostMapping("/memberList/pause") // 휴회진행
    public String pauseMembership(@ModelAttribute("pauseForm") RestEntity restEntity,
                                  @RequestParam String phone, Model model) {
        try {
            // 회원 정보 가져오기
            MemberDTO memberDTO = memberService.findByPhoneNumber(phone);
            if (memberDTO == null) {
                throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
            }

            // 남은 연기 횟수가 있는 경우에만 -1 감소
            int restCount = memberDTO.getRestcount();
            if (restCount > 0) {
                memberDTO.setRestcount(restCount - 1);
                memberService.updateRestCount(phone, restCount - 1);
            }


            // 회원권 종료일 변경
            memberService.updateMembershipEndDate(phone, restEntity.getDelayDays());
            // 락카 종료일 변경
            memberService.updateLockerEndDate(phone, restEntity.getDelayDaysForLocker());
            // 운동복 종료일 변경
            memberService.updateShirtEndDate(phone, restEntity.getDelayDaysForShirt());
            restEntity.setStatus("approved");
            RestService.savePauseRequest(restEntity);
            return "redirect:/adminPage";
        } catch (EntityNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public String handleMembershipNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
    @GetMapping("/membershipList")
    public String membershipListPage(@RequestParam(value="searchName", required = false) String searchName, Model model){
       List<MembershipDTO> membershipDTOList = new ArrayList<>();

        if (searchName != null && !searchName.trim().isEmpty()) {
            membershipDTOList = memberService.findByNameship(searchName);  // 검색어가 있을 때만 검색
       } else {
           membershipDTOList = memberService.findAlls();
       }
       Collections.reverse(membershipDTOList); // This reverses the order of the list
       model.addAttribute("memberships", membershipDTOList);



       return "membershipList";
    }

}
