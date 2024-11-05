package project.gym.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="cost_table")
public class CostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // 구분하는거 AS or 지출
    private String applicant; //신청자
    private LocalDate applicantDate; // 신청일자
    private String product; // 기구명 or 구매품목
    private String details; // 고장이유 or 용량 크기
    private String fixed; // 수리내용 or 수량
    private LocalDate receiveDate; //접수일자 or 구매일자
    private String pay; //결제금액
    private String shop; //업체명 or 구매명(업체명)
    private String phones; // 업체연락처 or 구매 연락처

    private LocalDate finished; // 완료일자 or..

    private Integer cost; // .. or 단가
    private Integer payfull; // .. or 물품 금액
    private Integer delivery; // .. or 배송금액


}
