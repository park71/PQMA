package project.gym.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class RequestDTO {

    private Integer id;

    private String coach;
    private String name;
    private String gender;

    private String phone;
    private String kakao;

    private String membership;
    private LocalDate memstart;
    private LocalDate memend;

    private String locker;
    private Integer locknum;
    private LocalDate lockstart;
    private LocalDate lockend;

    private String shirt;
    private LocalDate shirtstart;
    private LocalDate shirtend;

    private String reason;

    private LocalDate startDate;
    private LocalDate endDate;
}
