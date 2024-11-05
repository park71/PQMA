package project.gym.member.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="UserEntity")
public class UserEntity { //홈페이지 회원가입 목록
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;




    @Column(unique = true)
    private String useryd;

    private String username;
    private String password;

    @Column(length= 10)
    private String sex;

    private LocalDate bir;
    private String phnum;
    private String address;
    private String email;

    private String role;

    @OneToMany(mappedBy = "user")
    private Set<ConsultationEntity> consultations;

    private String resetToken; // 비밀번호찾기 로직
//
//    private String profileImage; // 카카오 프로필 이미지 URL
//
//    private String nickname; // 카카오 사용자 닉네임

   // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
   // private MemberEntity member;


}
