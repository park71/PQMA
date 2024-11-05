package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@Table(name = "locker_history")
public class LockHistoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer locknum; // 락카번호

    private String memnum; // 회원번호
    private String name; // 회원이름
    private String locker; //락카종류
    private Integer lockpass; // 락카 비밀번호
    private LocalDate lockstart; // 락카 시작일자
    private LocalDate lockend; // 락카 종료일

    private String status;
    @Transient
    private Long count; // 경과 일수 저
    public long getCount() {
        // lockEndDate가 null이 아니면 오늘과의 차이를 계산하고, null이면 0 반환
        return lockend != null ? ChronoUnit.DAYS.between(lockend, LocalDate.now()) : 0;
    }


}
