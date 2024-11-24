package project.gym.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="Inbody_list")
public class InbodyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String file_path;
    private LocalDateTime upload_time;
    private LocalDate births;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    @Column(name = "record_date")
    private LocalDate recordDate; // 기록날자

}
