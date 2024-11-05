package project.gym.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {






    private Long fromMemberId;
    private String fromMemberName;
    private String fromMemberPhone;
    private String fromMemberKakao;
    private LocalDate fromMemberMemstart;
    private LocalDate fromMemberMemend;
    private long fromMemberRemaindays;
    private String fromMemberlocker;
    private Integer fromMemberlocknum;
    private LocalDate fromMemberlockstart;
    private LocalDate fromMemberlockend;
    private String fromMembershirt;
    private LocalDate fromMembershirtstart;
    private LocalDate fromMembershirtend;



    private Long toMemberId;
    private String toMemberName;
    private String toMemberPhone;
    private String toMemberKakao;
    private LocalDate toMemberMemstart;
    private LocalDate toMemberMemend;
    private long toMemberRemaindays;
    private String toMemberlocker;
    private Integer toMemberlocknum;
    private LocalDate toMemberlockstart;
    private LocalDate toMemberlockend;
    private String toMembershirt;
    private LocalDate toMembershirtstart;
    private LocalDate toMembershirtend;


    private String status;


    private int daysToTransfer; //회원권 양도 일수

    private LocalDateTime applicationDate; // 회원권 신청 시간

    private String price;

}
