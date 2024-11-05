package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeConsultDTO {

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
