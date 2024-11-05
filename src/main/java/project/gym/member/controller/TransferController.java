package project.gym.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gym.member.dto.TransferDTO;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.TransferEntity;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.TrasnferRepository;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "https://www.priqma.com")
public class TransferController {

    @Autowired
    private TrasnferRepository transferRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransferService transferService;

    @Autowired
    private MemberRepository memberRepository;


    @PostMapping("/transfer")
    public String transferMembership(@RequestBody TransferDTO transferDTO, Model model) {
        System.out.println("hi");
        transferService.transferMembership(transferDTO.getFromMemberId(), transferDTO.getToMemberId(), transferDTO.getDaysToTransfer());
        System.out.println("next Level");
        return "redirect:/adminPage";
    }


    @PostMapping("/trans")
    public String transferMemberships(@RequestBody TransferDTO transferDTO, Model model) {
        // TransferDTO에서 ID 값 추출
        Long fromMemberId = transferDTO.getFromMemberId();
        Long toMemberId = transferDTO.getToMemberId();
        // ID를 이용해 MemberEntity 조회
        MemberEntity fromMember = (MemberEntity) memberRepository.findById(fromMemberId)
                .orElseThrow(() -> new RuntimeException("From Member not found"));
        MemberEntity toMember = (MemberEntity) memberRepository.findById(toMemberId)
                .orElseThrow(() -> new RuntimeException("To Member not found"));
        // TransferEntity에 설정
        TransferEntity transfer = new TransferEntity();
        transfer.setFromMember(fromMember);
        transfer.setFromMemberName(fromMember.getName());
        transfer.setFromMemberPhone(fromMember.getPhone());
        transfer.setFromMemberKakao(fromMember.getKakao());
        transfer.setFromMemberMemstart(fromMember.getMemstart());
        transfer.setFromMemberMemend(fromMember.getMemend());
        transfer.setFromMemberRemaindays(fromMember.getRemainDays());
        transfer.setFromMemberlocker(fromMember.getLocker());
        transfer.setFromMemberlocknum(fromMember.getLocknum());
        transfer.setFromMemberlockstart(fromMember.getLockstart());
        transfer.setFromMemberlockend(fromMember.getLockend());
        transfer.setFromMembershirt(fromMember.getShirt());
        transfer.setFromMembershirtstart(fromMember.getShirtstart());
        transfer.setFromMembershirtend(fromMember.getShirtend());

        //
        transfer.setToMember(toMember);
        transfer.setToMemberName(toMember.getName());
        transfer.setToMemberPhone(toMember.getPhone());
        transfer.setToMemberKakao(toMember.getKakao());
        transfer.setToMemberMemstart(toMember.getMemstart());
        transfer.setToMemberMemend(toMember.getMemend());
        transfer.setToMemberRemaindays(toMember.getRemainDays());
        transfer.setToMemberlocker(toMember.getLocker());
        transfer.setToMemberlocknum(toMember.getLocknum());
        transfer.setToMemberlockstart(toMember.getLockstart());
        transfer.setToMemberlockend(toMember.getLockend());
        transfer.setToMembershirt(toMember.getShirt());
        transfer.setToMembershirtstart(toMember.getShirtstart());
        transfer.setToMembershirtend(toMember.getShirtend());

        transfer.setDaysToTransfer(transferDTO.getDaysToTransfer());
        transfer.setPrice(transferDTO.getPrice());
        transfer.setStatus("pending"); // 상태 설정

        // TransferEntity 저장
        transferRepository.save(transfer);

        System.out.println("next Level2");
        return "redirect:/userdashboard";
    }

}
