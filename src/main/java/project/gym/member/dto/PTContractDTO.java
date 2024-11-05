package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.PTContractEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PTContractDTO {

    private Long id;

    private String coach; //담당 선생님
    private String name;
    private String gender;

    private String phone; // Ensure the database column name matches

    private LocalDate birth;
    private String address;
    private String kakao;

    private String purpose; //운동목적

    private String ptmembership; // 피티종류
    private LocalDate ptstart; // 피티 시작일
    private String count; // 피티 남은 횟수
    private String credit; // 결제 방법

    private String status;
    private LocalDateTime applicationDate;
    private String signature;


    private String price;


    public static PTContractDTO ptDTOs(PTContractEntity ptContractEntity) {
        PTContractDTO ptContractDTO = new PTContractDTO();
        ptContractDTO.setId(ptContractEntity.getId());
        ptContractDTO.setCoach(ptContractEntity.getCoach());
        ptContractDTO.setName(ptContractEntity.getName());
        ptContractDTO.setGender(ptContractEntity.getGender());
        ptContractDTO.setPhone(ptContractEntity.getPhone());
        ptContractDTO.setBirth(ptContractEntity.getBirth());
        ptContractDTO.setAddress(ptContractEntity.getAddress());
        ptContractDTO.setKakao(ptContractEntity.getKakao());
        ptContractDTO.setPurpose(ptContractEntity.getPurpose());
        ptContractDTO.setPtmembership(ptContractEntity.getPtmembership());
        ptContractDTO.setCredit(ptContractEntity.getCredit());
        ptContractDTO.setPtstart(ptContractEntity.getPtstart());
        ptContractDTO.setCount(String.valueOf(ptContractEntity.getCount()));
        ptContractDTO.setStatus(ptContractEntity.getStatus());
        ptContractDTO.setApplicationDate(ptContractEntity.getApplicationDate());
        ptContractDTO.setPrice(ptContractEntity.getPrice());
        ptContractDTO.setSignature(ptContractDTO.getSignature());

        return ptContractDTO;
    }
}
