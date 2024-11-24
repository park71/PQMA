package project.gym.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import project.gym.member.service.GoogleSheetsService;
import project.gym.member.service.MemberService;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private MemberService memberService;
    @Autowired
    private GoogleSheetsService googleSheetsService;

    // 매일 오전 1시에 장기미출석자 업데이트
    @Scheduled(cron = "0 0 1 * * *")
    public void scheduleAbsentMembersUpdate() {
        System.out.println("장기 미출석자 업데이트 중...");
        memberService.updateAbsentMembers();
    }

    @Scheduled(cron = "0 10 1 * * *")
    public void scheduleAbsentReMembersUpdate() {
        System.out.println("장기 미출석자 최신화 중....");
        memberService.updateLongTermAbsentMembersStatus(); // 서비스 메소드 호출
    }

    // 매일 새벽 1시 20분에 QR코드 삭제 작업 수행
    @Scheduled(cron = "0 20 1 * * ?")  // cron 표현식을 사용하여 매일 1시 20분에 실행
    public void scheduleQrCodeCleanup() {
        memberService.removeExpiredQrCodes();  // 회원 서비스의 메서드 호출
    }

    @Scheduled(cron = "0 30 1 * * *")
    public void scheduleQrcodeSetup(){
        memberService.generateQRCodeForMembersWithActiveMemberships();
    }
    @Scheduled(cron = "0 0 2 * * *") // 남은일수가 30이리상지난사람들 구글로 보내버리기
    public void schedulegoogleSet(){
        googleSheetsService.syncDataToSheets();
    }

}

