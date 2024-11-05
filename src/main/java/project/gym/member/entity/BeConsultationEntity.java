package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="beconsult_list")
public class BeConsultationEntity { // 비회원 상담

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String gender;
    private String mail;
    private String phonenumber;
    private String exercise;
    private String sangType;
    private LocalDateTime sangTime;
    private String note;


}
