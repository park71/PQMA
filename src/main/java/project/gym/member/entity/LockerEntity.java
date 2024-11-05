package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "locker", uniqueConstraints = @UniqueConstraint(columnNames = "locknum"))
public class LockerEntity { // 락커 DB

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
    @Column(nullable = false)
    private Boolean isOccupied = false; // 기본값을 false로 설정

//
//     락카 폐기물 상태 / 보관, 폐기, 부여
     private String status;


    public LockerEntity(){

    }

    public LockerEntity(Integer locknum, Boolean isOccupied) {
        this.locknum=locknum;
        this.isOccupied = isOccupied;

    }


}
