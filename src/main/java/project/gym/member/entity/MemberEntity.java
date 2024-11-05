package project.gym.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="member_list")
public class MemberEntity { //회원권 DB
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String coach;

    private String name;

    private String gender;
    private String content; // 메모장
    @Column(name = "phone")
    private String phone; // Ensure the database column name matches

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose;
    private String comein;
    private String membership;
    private String credit;
    private Integer price; // 결제 금액
    private LocalDate memstart;
    private LocalDate memend;

    @Setter
    private Long remainDays;


    private String locker;

    @Column(nullable = true) // or you can remove the nullable attribute
    private Integer locknum;

    private LocalDate lockstart;
    private LocalDate lockend;

    private String shirt;
    private LocalDate shirtstart;
    private LocalDate shirtend;

    private String status; //(pending, approved, rejected)

    @Column(name = "applicationDate", updatable = false)
    private LocalDateTime applicationDate;


    private String signature; // 서명 데이터 추가

    private String stat; //회원 상태
    private String qrCodePath; // QR 코드 파일 경로 저장;

    private String ring;


    private String longTime;
    // 추가: 인바디 기록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InbodyEntity> inbodyRecords;


    @OneToMany(mappedBy = "fromMember")
    @JsonManagedReference
    private List<TransferEntity> transfersFrom;

    @OneToMany(mappedBy = "toMember")
    @JsonManagedReference
    private List<TransferEntity> transfersTo;

    @OneToMany(mappedBy = "member")
    private List<EntryRecordEntity> enterRecords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closer_id") // 'closer_id' 외래 키 추가
    private CloserEntity closerEntity; // CloserEntity와의 관계


    public long getRemainDays() {
        return (remainDays != null) ? remainDays : 0L;  // 기본값 0L
    }


    //  @OneToOne
   // @JoinColumn(name = "user_id")
   // private UserEntity user;



    @PreUpdate
    @PrePersist
    public void calculateRemainDays() {
        if (memend != null && memstart != null) {
            this.remainDays = ChronoUnit.DAYS.between(LocalDate.now(), memend);
        } else {
            this.remainDays = 0L;
        }
        this.applicationDate = LocalDateTime.now(); // 엔티티가 처음 저장될 때 자동으로 신청 시간을 현재 시간으로 설정
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembershipEntity> contracts = new ArrayList<>(); // 한명이 여러개의 계약서를 등록할수 있음

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<EntryRecordEntity> entryRecords = new ArrayList<>(); // 출입 기록

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MembershipEntity> memberships;


    // 생년월일의 '일' 부분만 반환하는 헬퍼 메서드
    public int getBirthDay() {
        return birth != null ? birth.getDayOfMonth() : 0;
    }


    public String getName() {
        return name != null ? name : "";
    }
}
