package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.MembershipEntity;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO {

    private Long id;

    private String phone;
    private String name;
    private String coach;
    private String gender;

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose;
    private String comein;
    private String membership;
    private String credit;
    private LocalDate memstart;
    private LocalDate memend;

    private String locker;
    private Integer locknum;
    private LocalDate lockstart;
    private LocalDate lockend;

    private Integer price;
    private Integer  totalprice;

    private String shirt;
    private LocalDate shirtstart;
    private LocalDate shirtend;

    private String content;

    private long remainDays;
    private Long member_id;  // member의 id

    public MembershipDTO(Long id, String name, MemberEntity member, LocalDate memstart, LocalDate memend, String locker, Integer locknum, LocalDate lockstart, LocalDate lockend, String shirt, LocalDate shirtstart, LocalDate shirtend, Integer price, Integer totalprice, String content) {
    }

    public static MembershipDTO MembershipsDTO(MembershipEntity membership){
        MembershipDTO membershipDTO = new MembershipDTO();
        membershipDTO.setId(membership.getId());
        membershipDTO.setName(membership.getName());
        membershipDTO.setMembership(membership.getMembership());
        membershipDTO.setMemstart(membership.getMemstart());
        membershipDTO.setMemend(membership.getMemend());
        membershipDTO.setCoach(membership.getCoach());
        membershipDTO.setPhone(membership.getPhone());
        membershipDTO.setPurpose(membershipDTO.getPurpose());
        membershipDTO.setComein(membershipDTO.getComein());
        membershipDTO.setAddress(membership.getAddress());
        membershipDTO.setGender(membership.getGender());
        membershipDTO.setShirt(membership.getShirt());
        membershipDTO.setShirtstart(membership.getShirtstart());
        membershipDTO.setShirtend(membership.getShirtend());
        membershipDTO.setCredit(membership.getCredit());
        membershipDTO.setPrice(membership.getPrice());
        membershipDTO.setCredit(membership.getCredit());
        membershipDTO.setTotalprice(membership.getTotalprice());
        membershipDTO.setBirth(membership.getBirth());
        membershipDTO.setKakao(membership.getKakao());
        membershipDTO.setLocker(membership.getLocker());
        membershipDTO.setLocknum(membership.getLocknum());
        membershipDTO.setLockstart(membership.getLockstart());
        membershipDTO.setLockend(membership.getLockend());
        membershipDTO.setContent(membership.getContent());
        // MemberEntity에서 ID 추출
        membershipDTO.setMember_id(Long.valueOf(membership.getMember().getId()));
        return  membershipDTO;
    }


}
