package project.gym.member.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.LockerEntity;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LockerDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer locknum; // 락카번호

    private String memnum;
    private String name; // 락카회원이름
    private String locker; //락카종류
    private int lockpass; // 락카 비밀번호
    private LocalDate lockstart; // 락카 시작일자
    private LocalDate lockend; // 락카 종료일
    private Boolean isOccupied; // 유무확인


        // 락카 폐기물 상태 / 보관, 폐기, 부여
     private String status;


    private List<Long> lockerIds;

    // LockerEntity를 인수로 받는 생성자 추가
    public LockerDTO(LockerEntity lockerEntity) {
        this.id = lockerEntity.getId();
        this.locknum = lockerEntity.getLocknum();
        this.memnum = lockerEntity.getMemnum();
        this.locker = lockerEntity.getLocker();
        this.lockpass = lockerEntity.getLockpass() != null ? lockerEntity.getLockpass() : 0;
        this.lockstart = lockerEntity.getLockstart();
        this.lockend= lockerEntity.getLockend();
        this.isOccupied= lockerEntity.getIsOccupied();
        this.status = lockerEntity.getStatus();
    }
}
