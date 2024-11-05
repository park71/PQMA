package project.gym.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="transfer_list")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_member_id")
    @JsonBackReference
    private MemberEntity fromMember;
    private String fromMemberName;
    private String fromMemberPhone;
    private String fromMemberKakao;
    private LocalDate fromMemberMemstart;
    private LocalDate fromMemberMemend;
    private long fromMemberRemaindays;
    private String fromMemberlocker;
    private Integer fromMemberlocknum;
    private LocalDate fromMemberlockstart;
    private LocalDate fromMemberlockend;
    private String fromMembershirt;
    private LocalDate fromMembershirtstart;
    private LocalDate fromMembershirtend;


    @ManyToOne
    @JoinColumn(name = "to_member_id")
    @JsonBackReference
    private MemberEntity toMember;
    private String toMemberName;
    private String toMemberPhone;
    private String toMemberKakao;
    private LocalDate toMemberMemstart;
    private LocalDate toMemberMemend;
    private long toMemberRemaindays;
    private String toMemberlocker;
    private Integer toMemberlocknum;
    private LocalDate toMemberlockstart;
    private LocalDate toMemberlockend;
    private String toMembershirt;
    private LocalDate toMembershirtstart;
    private LocalDate toMembershirtend;


    private String status;


    private int daysToTransfer; //회원권 양도 일수

    private LocalDateTime applicationDate; // 회원권 신청 시간

    private String price;

    // Multiple MemberEntities can be associated with one CloserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closer_id")
    private CloserEntity closerEntity; // Reference to CloserEntity

    @PrePersist
    public void prePersist() {
        this.applicationDate = LocalDateTime.now(); // 엔티티가 처음 저장될 때 자동으로 신청 시간을 현재 시간으로 설정
    }


}
