package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.MemberEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {



    private Integer id;
    private String phone;
    private String coach;
    private String name;
    private String gender;
    private String content;

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose;
    private String comein;
    private String membership;
    private String credit;
    private Integer price;
    private LocalDate memstart;
    private LocalDate memend;

    private String locker;
    private Integer locknum;
    private LocalDate lockstart;
    private LocalDate lockend;

    private String shirt;
    private LocalDate shirtstart;
    private LocalDate shirtend;

    private long remainDays;

    private String status;
    private LocalDateTime applicationDate; // 회원권 신청 시간


    private List<MembershipDTO> memberships; // 여러 계약 정보를 담기 위한 리스트


    private String signature; // 서명 데이터 추가
    private String stat; // 회원상태
    private LocalDateTime lastAttendance;
    private String qrCodePath;
    private String longTime; // 장기결석 특이사항
    private String ring; //전화 유무
    private String inbody; //인바디

    private String profile;  // 파일 경로를 저장할 필드
    private String profileImage;  // 파일 업로드를 위한 필드
    private Integer restcount; // 휴회 카운트
    // 인자를 받는 생성자
    public MemberDTO(String name, String phone, String kakao, String membership, LocalDate memstart, LocalDate memend, long remainDays,
                     String locker, Integer locknum, LocalDate lockstart, LocalDate lockend, String shirt,
                     LocalDate shirtstart, LocalDate shirtend, String signature, Integer restcount) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.kakao = kakao;
        this.membership = membership;
        this.memstart=memstart;
        this.memend=memend;
        this.remainDays=remainDays;
        this.locker = locker;
        this.lockstart=lockstart;
        this.lockend=lockend;
        this.locknum=locknum;
        this.shirt=shirt;
        this.shirtstart=shirtstart;
        this.shirtend=shirtend;
        this.signature=signature;
        this.restcount=restcount;

    }

    // MemberEntity를 통해 DTO를 생성하는 생성자
    public MemberDTO(MemberEntity member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.kakao = member.getKakao();
        this.memstart = member.getMemstart();
        this.memend = member.getMemend();
        this.remainDays = member.getRemainDays();
        this.locker = member.getLocker();
        this.locknum = member.getLocknum();
        this.lockstart = member.getLockstart();
        this.lockend = member.getLockend();
        this.shirt = member.getShirt();
        this.shirtstart = member.getShirtstart();
        this.shirtend = member.getShirtend();
        this.restcount=member.getRestcount();
    }

    // Optional을 이용한 DTO 생성 메서드
    public static MemberDTO fromOptional(Optional<MemberEntity> optionalMember) {
        if (optionalMember.isPresent()) {
            return new MemberDTO(optionalMember.get());
        } else {
            return null; // 또는 예외를 던질 수도 있습니다
        }
    }
    public static MemberDTO MembershipDTO(MemberEntity memberEntity){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setCoach(memberEntity.getCoach());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setGender(memberEntity.getGender());
        memberDTO.setContent(memberEntity.getContent());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setBirth(memberEntity.getBirth());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setKakao(memberEntity.getKakao());
        memberDTO.setPurpose(memberEntity.getPurpose());
        memberDTO.setComein(memberEntity.getComein());
        memberDTO.setMembership(memberEntity.getMembership());
        memberDTO.setCredit(memberEntity.getCredit());
        memberDTO.setPrice(memberEntity.getPrice());
        memberDTO.setMemstart(memberEntity.getMemstart());
        memberDTO.setMemend(memberEntity.getMemend());
        memberDTO.setLocker(memberEntity.getLocker());
        memberDTO.setLocknum(memberEntity.getLocknum());
        memberDTO.setLockstart(memberEntity.getLockstart());
        memberDTO.setLockend(memberEntity.getLockend());
        memberDTO.setShirt(memberEntity.getShirt());
        memberDTO.setShirtstart(memberEntity.getShirtstart());
        memberDTO.setShirtend(memberEntity.getShirtend());
        memberDTO.setRemainDays(memberEntity.getRemainDays());
        memberDTO.setStatus(memberEntity.getStatus());
        memberDTO.setApplicationDate(memberEntity.getApplicationDate());
        memberDTO.setQrCodePath(memberEntity.getQrCodePath());
        memberDTO.setContent(memberEntity.getContent());
        memberDTO.setProfile(String.valueOf(memberEntity.getProfile()));
        memberDTO.setRestcount(memberEntity.getRestcount());
        memberDTO.setProfileImage(memberEntity.getProfileImage());

        return  memberDTO;
    }



}
