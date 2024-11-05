package project.gym.member.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.EntryRecordEntity;
import project.gym.member.entity.MemberEntity;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CloserDTO {

    private Long id;


    private List<MemberEntity> members; //오늘 계약한 회원들 가져올 때 사용 -> QR코드 까지 가지고와지면좋을듯

    private EntryRecordEntity entryRecord; // 오늘 출입한 회원수
    private String revenue; // 오늘 매출 -> 이것도 오늘 계약한 사람들 price값들 가져와서 다 더한 값을 여기에 반영
    private String money; // 오늘 시재
    private String fileName;  //영수증 모음들
    private String filePath;
    private String total_revenue; // 한달 매출  -->
    private LocalDate date;

}
