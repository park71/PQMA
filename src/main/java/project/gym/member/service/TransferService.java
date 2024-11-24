package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.MembershipEntity;
import project.gym.member.entity.TransferEntity;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.MembershipRepository;
import project.gym.member.repository.TrasnferRepository;

@Service
public class TransferService {

    @Autowired
    private TrasnferRepository transferRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    public TransferService(TrasnferRepository transferRepository, MemberService memberService) {
        this.transferRepository = transferRepository;
        this.memberService = memberService;
    }

    @Transactional
    public void transferMembership(Long fromMemberId, Long toMemberId, int daysToTransfer) {
        MemberEntity fromMember = (MemberEntity) memberRepository.findById(fromMemberId)
                .orElseThrow(() -> new RuntimeException("From Member not found"));
        MemberEntity toMember = (MemberEntity) memberRepository.findById(toMemberId)
                .orElseThrow(() -> new RuntimeException("To Member not found"));

        fromMember.calculateRemainDays();  // Ensure remainDays is calculated
        toMember.calculateRemainDays();  // Ensure remainDays is calculated

        if (fromMember.getRemainDays() < daysToTransfer) {
            throw new RuntimeException("Insufficient remaining days to transfer");
        }

// Update fromMember
        fromMember.setMemend(fromMember.getMemend().minusDays(daysToTransfer));
        toMember.setRestcount(toMember.getRestcount() + fromMember.getRestcount());


// Update toMember
        toMember.setMemend(toMember.getMemend().plusDays(daysToTransfer));
        fromMember.setRestcount(0);  // Reset fromMember's restcount to 0
// Transfer restcount from fromMember to toMember (Add restcount)



        // Save TransferEntity
        TransferEntity transfer = new TransferEntity();
        transfer.setFromMember(fromMember);
        transfer.setFromMemberName(fromMember.getName());
        transfer.setFromMemberPhone(fromMember.getPhone());
        transfer.setFromMemberKakao(fromMember.getKakao());
        transfer.setFromMemberMemstart(fromMember.getMemstart());
        transfer.setFromMemberMemend(fromMember.getMemend());
        transfer.setFromMemberRemaindays(fromMember.getRemainDays());


        transfer.setToMember(toMember);
        transfer.setToMemberName(toMember.getName());
        transfer.setToMemberPhone(toMember.getPhone());
        transfer.setToMemberKakao(toMember.getKakao());
        transfer.setToMemberMemstart(toMember.getMemstart());
        transfer.setToMemberMemend(toMember.getMemend());
        transfer.setToMemberRemaindays(toMember.getRemainDays());

        transfer.setDaysToTransfer(daysToTransfer);

        transfer.setStatus("approved");
        transferRepository.save(transfer);

        // Save updated members
        memberRepository.save(fromMember);
        memberRepository.save(toMember);
    }

    @Transactional
    public void transferMemberships(Long fromMemberId, Long toMemberId, int daysToTransfer) {
        MemberEntity fromMember = (MemberEntity) memberRepository.findById(fromMemberId)
                .orElseThrow(() -> new RuntimeException("From Member not found"));
        MemberEntity toMember = (MemberEntity) memberRepository.findById(toMemberId)
                .orElseThrow(() -> new RuntimeException("To Member not found"));


        System.out.println("양도일수"+ daysToTransfer);

        fromMember.calculateRemainDays();  // Ensure remainDays is calculated
        toMember.calculateRemainDays();  // Ensure remainDays is calculated

        if (fromMember.getRemainDays() < daysToTransfer) {
            throw new RuntimeException("Insufficient remaining days to transfer");
        }
        // Update fromMember's RemainDays
        fromMember.setRemainDays(fromMember.getRemainDays() - daysToTransfer);

        System.out.println("fromMember RemainDays after update: " + fromMember.getRemainDays());


// Update fromMember
        fromMember.setMemend(fromMember.getMemend().minusDays(daysToTransfer));
        toMember.setRestcount(toMember.getRestcount() + fromMember.getRestcount());


// Update toMember
        toMember.setMemend(toMember.getMemend().plusDays(daysToTransfer));
// Transfer restcount from fromMember to toMember (Add restcount)
        fromMember.setRestcount(0);  // Reset fromMember's restcount to 0

        // Update toMember's RemainDays
        toMember.setRemainDays(toMember.getRemainDays() + daysToTransfer);
        System.out.println("toMember RemainDays after update: " + toMember.getRemainDays());

        // Update fromMember
        if (fromMember.getMemend() != null) {
            fromMember.setMemend(fromMember.getMemend().minusDays(daysToTransfer));
            System.out.println("fromMember Memend after update: " + fromMember.getMemend());

        }

// 락커 양도 로직
        if (fromMember.getLocker() != null && fromMember.getLockend() != null) {
            // 락커 정보 전체 양도
            toMember.setLocker(fromMember.getLocker());
            toMember.setLocknum(fromMember.getLocknum());
            toMember.setLockstart(fromMember.getLockstart());
            toMember.setLockend(fromMember.getLockend());

            // fromMember의 락커 정보 초기화
            fromMember.setLocker(null);
            fromMember.setLocknum(null);
            fromMember.setLockstart(null);
            fromMember.setLockend(null);
        }

        // 운동복 양도 로직
        if (fromMember.getShirt() != null && fromMember.getShirtend() != null) {
            // 운동복 정보 전체 양도
            toMember.setShirt(fromMember.getShirt());
            toMember.setShirtstart(fromMember.getShirtstart());
            toMember.setShirtend(fromMember.getShirtend());

            // fromMember의 운동복 정보 초기화
            fromMember.setShirt(null);
            fromMember.setShirtstart(null);
            fromMember.setShirtend(null);
        }


//        if (fromMember.getShirtend() != null) {
//            fromMember.setShirtend(fromMember.getShirtend().minusDays(daysToTransfer));
//            System.out.println("fromMember Shirtend after update: " + fromMember.getShirtend());
//        }
//
//// Locker transfer logic
//        if (fromMember.getLocker() != null && fromMember.getLockend() != null) {
//            fromMember.setLockend(fromMember.getLockend().minusDays(daysToTransfer));
//            // Transfer locker and locknum from fromMember to toMember
//            toMember.setLocker(fromMember.getLocker());
//            toMember.setLocknum(fromMember.getLocknum());
//            toMember.setLockstart(fromMember.getLockstart());
//            toMember.setLockend(fromMember.getLockend());
//
//            // Clear locker and locknum from fromMember
//            fromMember.setLocker(null);
//            fromMember.setLocknum(null);
//        }

// Update toMember
        if (toMember.getMemend() != null) {
            toMember.setMemend(toMember.getMemend().plusDays(daysToTransfer));
        }
//
//        if (toMember.getLockend() != null) {
//            toMember.setLockend(toMember.getLockend().plusDays(daysToTransfer));
//        }
//
//        if (toMember.getShirtend() != null) {
//            toMember.setShirtend(toMember.getShirtend().plusDays(daysToTransfer));
//        }



        // Update or create a new MembershipEntity for fromMember MembershipEntity에 새로 값이 생성됨 여기 코드 넣자 그럼끝
        MembershipEntity fromMembership = new MembershipEntity();
        fromMembership.setMember(fromMember);
        fromMembership.setName(fromMember.getName());
        fromMembership.setCoach(fromMember.getCoach());
        fromMembership.setGender(fromMember.getGender());
        fromMembership.setAddress(fromMember.getAddress());
        fromMembership.setKakao(fromMember.getKakao());
        fromMembership.setComein(fromMember.getComein());
        fromMembership.setCredit(fromMember.getCredit());
        fromMembership.setPrice(fromMember.getPrice());
        fromMembership.setPurpose(fromMember.getPurpose());
        fromMembership.setMembership(fromMember.getMembership());
        fromMembership.setMemstart(fromMember.getMemstart());
        fromMembership.setMemend(fromMember.getMemend());
        fromMembership.setLocker(fromMember.getLocker());
        fromMembership.setLockstart(fromMember.getLockstart());
        fromMembership.setLockend(fromMember.getLockend());
        fromMembership.setLocknum(fromMember.getLocknum());
        fromMembership.setShirt(fromMember.getShirt());
        fromMembership.setShirtstart(fromMember.getShirtstart());
        fromMembership.setShirtend(fromMember.getShirtend());

        membershipRepository.save(fromMembership);

        // Update or create a new MembershipEntity for toMember
        MembershipEntity toMembership = new MembershipEntity();
        toMembership.setMember(toMember);
        toMembership.setName(toMember.getName());
        toMembership.setCoach(toMember.getCoach());
        toMembership.setGender(toMember.getGender());
        toMembership.setAddress(toMember.getAddress());
        toMembership.setKakao(toMember.getKakao());
        toMembership.setComein(toMember.getComein());
        toMembership.setCredit(toMember.getCredit());
        toMembership.setPrice(toMember.getPrice());
        toMembership.setPurpose(toMember.getPurpose());
        toMembership.setMembership(toMember.getMembership());
        toMembership.setMemstart(toMember.getMemstart());
        toMembership.setMemend(toMember.getMemend());
        toMembership.setLocker(toMember.getLocker());
        toMembership.setLockstart(toMember.getLockstart());
        toMembership.setLockend(toMember.getLockend());
        toMembership.setLocknum(toMember.getLocknum());
        toMembership.setShirt(toMember.getShirt());
        toMembership.setShirtstart(toMember.getShirtstart());
        toMembership.setShirtend(toMember.getShirtend());
        membershipRepository.save(toMembership);



        // Save TransferEntity
        TransferEntity transfer = new TransferEntity();

// FromMember 설정
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

// ToMember 설정
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

        transfer.setDaysToTransfer(daysToTransfer);
        transfer.setPrice(transfer.getPrice());

        transfer.setStatus("approved");
        transferRepository.save(transfer);

        // Save updated members
        memberRepository.save(fromMember);
        memberRepository.save(toMember);
    }

}
