package project.gym.member.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.RestEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String reason;
    private String phone;
    private int delayDaysForLocker;
    private int delayDaysForShirt;
    private int delayDays;

    private String lockprice; //락카 연기 비용


    private String status;
    private LocalDateTime applicationDate; // 회원권 신청 시간



    public static RestDTO PauseResponseDTO(RestEntity restEntity){
        RestDTO rest = new RestDTO();
        rest.setId(Math.toIntExact(restEntity.getId()));
        rest.setReason(restEntity.getReason());
        rest.setName(restEntity.getName());
        rest.setPhone(restEntity.getPhone());
        rest.setDelayDays(restEntity.getDelayDays());
        rest.setDelayDaysForLocker(restEntity.getDelayDaysForLocker());
        rest.setDelayDaysForShirt(restEntity.getDelayDaysForShirt());
        rest.setLockprice(restEntity.getLockprice());
        rest.setApplicationDate(restEntity.getApplicationDate());

        return rest;
    }
}

