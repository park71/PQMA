package project.gym.member.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="closerEntity")
public class CloserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "closerEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MemberEntity> members; // 오늘 등록한 회원들 리스트

    // CloserEntity.java
    @OneToMany(mappedBy = "closer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntryRecordEntity> entryRecords;

    private String revenue; // 오늘 매출 -> 이것도 오늘 계약한 사람들 price값들 가져와서 다 더한 값을 여기에 반영
    private String money; // 오늘 시재
    private String filePath;
    private String total_revenue; // 한달 매출  -->
    private LocalDate dateday;



}
