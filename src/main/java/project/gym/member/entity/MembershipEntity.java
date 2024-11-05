package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="member_list_full")
public class MembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String coach;
    private String content;
    private String name;
    private String gender;

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose;
    private String comein;
    private String membership;
    private String credit;
    private Integer price;
    private Integer totalprice;
    private LocalDate memstart;
    private LocalDate memend;

    private String locker;
    private Integer locknum;
    private LocalDate lockstart;
    private LocalDate lockend;

    private String shirt;
    private LocalDate shirtstart;
    private LocalDate shirtend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;


}
