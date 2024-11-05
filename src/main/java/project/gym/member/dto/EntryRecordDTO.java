package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.EntryRecordEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryRecordDTO {


    private Long id;

    private String name;
    private LocalDateTime entryTime;
    private String birthDay;
    private Long memberId;
    private String stat;

    // 포맷팅된 entryTime 반환
    public String getFormattedEntryTime() {
        if (entryTime != null) {
            return entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "";
    }

    public EntryRecordDTO(EntryRecordEntity entryRecord){
        this.id = entryRecord.getId();
        this.name=entryRecord.getName();
        this.entryTime = entryRecord.getEntryTime();
        this.birthDay= entryRecord.getBirthDay();
        this.memberId = Long.valueOf(entryRecord.getMember().getId());  // member의 id를 가져옴
        this.stat = entryRecord.getMember().getStat();
    }

}
