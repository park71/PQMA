package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="sangdam_list")
public class ConsultationEntity { // 상담 DB

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    private String sangdamType; // 상담종류
    private LocalDateTime sangdamTime; // 상담시간
    private String notes; //자세하 내용

    private String username;
    private String phnum;
    private String email;



}
