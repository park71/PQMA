package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="enterrecord")
@NoArgsConstructor
public class EntryRecordEntity { // 출입등록 DB

        @Id
        @GeneratedValue(strategy =  GenerationType.IDENTITY)
        private Long id;

        private LocalDateTime entryTime;
        private String name;
        private LocalDate birthDay;
        private String stat;

        @ManyToOne
        @JoinColumn(name = "member_id", nullable = false) // 'member_id'가 null이 될 수 없음을 명시
        private MemberEntity member;

        // EntryRecordEntity.java
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "closer_id") // closer_id 외래 키 설정
        private CloserEntity closer;
        // 생성
        public EntryRecordEntity(MemberEntity member, LocalDateTime entryTime, String name) {
            this.entryTime = LocalDateTime.now();
            this.member = member;
            this.name=name;
        }

    // 생년월일의 '일' 부분만 반환하는 헬퍼 메서드
    public String getBirthDay() {
        return birthDay != null ? String.valueOf(birthDay.getDayOfMonth()) : null;
    }

}
