package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="rest_list")
public class RestEntity { // 회원권 휴회 DB

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private int delayDays; // 회원권 연기
    private int delayDaysForLocker; // 락커연기
    private int delayDaysForShirt; // 운동복 연기
    private String reason;

    private String lockprice; //락카 연기 비용


    private String status;
    private LocalDateTime applicationDate; // 회원권 신청 시간

    // Multiple MemberEntities can be associated with one CloserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closer_id")
    private CloserEntity closerEntity; // Reference to CloserEntity

    @PrePersist
    public void prePersist() {
        this.applicationDate = LocalDateTime.now(); // 엔티티가 처음 저장될 때 자동으로 신청 시간을 현재 시간으로 설정
    }
}
