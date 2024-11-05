package project.gym.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.gym.member.dto.MemberDTO;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.MemberService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
//@CrossOrigin(origins = "https://www.priqma.com")
@Slf4j
public class MemberController { //양도전용

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }




    // 회원 ID로 특정 회원의 정보를 반환하는 API
    @GetMapping("/{memberId}")
    public MemberEntity getMemberById(@PathVariable Long memberId) {
        return memberService.getMemberById(memberId);
    }

    @GetMapping("/search")
    public List<MemberDTO> searchMembers(@RequestParam("query") String query) {
        System.out.println("Search query received: " + query);
        log.info("Search query received: {}", query);

        List<MemberEntity> allMembers = memberService.getAllMembers();
        log.info("Filtering results for query: {}", query);

        log.info("All members retrieved: {}", allMembers.size()); // Check if all members are retrieved

        List<MemberEntity> result = allMembers.stream()
                .filter(member -> member.getName() != null && member.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(member -> member.getStatus() == null || "approved".equals(member.getStatus()))
                .collect(Collectors.toList());
        log.info("Search results: {}", result);

        return result.stream()
                .map(member -> new MemberDTO(
                        member.getId(),
                        member.getName(),
                        member.getPhone(),
                        member.getKakao(),
                        member.getMembership(),
                        member.getMemstart(),
                        member.getMemend(),
                        member.getRemainDays(),
                        member.getLocker(),
                        member.getLocknum(),
                        member.getLockstart(),
                        member.getLockend(),
                        member.getShirt(),
                        member.getShirtstart(),
                        member.getShirtend(),
                        member.getSignature()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/loggedin")
    public ResponseEntity<MemberDTO> getLoggedInMember(HttpSession session) {
        System.out.println("회원 찾아오자");

        String useryd = (String) session.getAttribute("loginUseryd");

        // 로그인한 사용자의 UserEntity 가져오기
        UserEntity user = userRepository.findByUseryd(useryd);

        // 전화번호로 회원을 조회합니다.
        Optional<MemberEntity> member = memberService.findByPhoneing(user.getPhnum());

        if (member.isPresent()) {
            MemberDTO memberDTO = new MemberDTO(member.get());
            return ResponseEntity.ok(memberDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }




}
