package project.gym.member.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="ptcontractentity")
public class PTContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String coach; //담당 선생님
    private String name;
    private String gender;

    @Column(name = "phone")
    private String phone; // Ensure the database column name matches

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose; //운동목적

    private String ptmembership; // 피티종류
    private LocalDate ptstart; // 피티 시작일
    private Integer count; // 피티 남은 횟수
    private String credit; // 결제 방법

    private String price;

    private String status;
    private LocalDateTime applicationDate; // 회원권 신청 시간
    private String signature;


    @PrePersist
    public void prePersist() {
        this.applicationDate = LocalDateTime.now(); // 엔티티가 처음 저장될 때 자동으로 신청 시간을 현재 시간으로 설정
    }

    @OneToMany(mappedBy = "ptContractId")
    private List<DecrementRecord> decrementRecords;
    // Multiple MemberEntities can be associated with one CloserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closer_id")
    private CloserEntity closerEntity; // Reference to CloserEntity





}
