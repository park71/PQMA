package project.gym.member.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.entity.CloserEntity;
import project.gym.member.entity.EntryRecordEntity;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.PTContractEntity;
import project.gym.member.repository.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CloserService {


    @Autowired
    private EntryRecordRepository entryRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CloserRepository closerRepository;

    @Autowired
    private RestRepository restRepository;

    @Autowired
    private TrasnferRepository trasnferRepository;

    @Autowired
    private PTContractRepository ptContractRepository;

    @Autowired
    private CostRepository costRepository;


    public List<CloserEntity> getRevenueByDates(LocalDate date) {
        return closerRepository.findByDateday(date); // 데이터베이스에서 날짜별 CloserEntity 조회
    }
    public Integer getRevenueByDate(LocalDate date) {
        return closerRepository.findRevenueByDate(date); // date는 LocalDate
    }
    public List<MemberEntity> getPriceByCreditTypeAndToday(String creditType) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return memberRepository.findByCreditAndApplicationDateBetween(creditType, startOfDay, endOfDay);
    }


    public List<EntryRecordEntity> getTodayAttendance() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 내일 00:00:00

        return entryRecordRepository.findTodayAttendance(startOfDay, endOfDay); // 오늘 출입 현황 가져오기
    }
    public Map<String, Long> getAttendanceCountsByHour() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<EntryRecordEntity> entries = entryRecordRepository.findTodayAttendance(startOfDay, endOfDay);

        Map<String, Long> entryCountsByHour = new HashMap<>();

        // 6시부터 23시까지 시간대 초기화 (출석 기록이 없더라도 0으로 표시되도록)
        for (int hour = 6; hour <= 23; hour++) {
            String hourSlot = hour + "시";
            entryCountsByHour.put(hourSlot, 0L); // 초기 값 0으로 설정
        }

        // 모든 시간대의 출석 현황 집계
        for (EntryRecordEntity entry : entries) {
            LocalDateTime entryTime = entry.getEntryTime();
            int hour = entryTime.getHour();

            // 6시부터 23시까지만 집계
            if (hour >= 6 && hour <= 23) {
                String hourSlot = hour + "시";
                entryCountsByHour.put(hourSlot, entryCountsByHour.getOrDefault(hourSlot, 0L) + 1);
            }
        }

        return entryCountsByHour;
    }
    public List<MemberEntity> getTodayRegistrations() {

        return memberRepository.findTodayRegistrations(); // 오늘 등록한 회원 리스트 가져오기
    }

    public List<PTContractEntity> getTodayRegistration(){
        return ptContractRepository.findTodayRegistration();
    }

    public Map<String, Integer> getTodayRegistrationRevenueDetails() {
        Map<String, Integer> revenueDetails = new HashMap<>();

        // Calculate revenue for each entity
        Integer memberRevenue = memberRepository.calculateTodayRegistrationRevenue();
        Integer ptRevenue = ptContractRepository.calculateTodayPTRevenue();
        Integer restRevenue = restRepository.calculateTodayRestRevenue();
        Integer transRevenue = trasnferRepository.calculateTodayTransRevenue();

        // Populate map with null-safe values
        revenueDetails.put("memberRevenue", memberRevenue != null ? memberRevenue : 0);
        revenueDetails.put("ptRevenue", ptRevenue != null ? ptRevenue : 0);
        revenueDetails.put("restRevenue", restRevenue != null ? restRevenue : 0);
        revenueDetails.put("transRevenue", transRevenue != null ? transRevenue : 0);

        // Calculate the total revenue and add to map
        int todayRevenue = revenueDetails.values().stream().mapToInt(Integer::intValue).sum();
        revenueDetails.put("todayRevenue", todayRevenue);

        return revenueDetails;
    }



    // 주간 일별 회원 등록 매출 합계 계산
    public Map<String, Integer> calculateWeeklyRegistrationRevenue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Map<String, Integer> weeklyRevenueMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            Integer dailyRevenue = memberRepository.calculateDailyRegistrationRevenue(date);
            weeklyRevenueMap.put(sdf.format(date), dailyRevenue != null ? dailyRevenue : 0);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weeklyRevenueMap;
    }

    // closerService.java

//    //월간 총매출비용
//    public Integer calculateMonthlyRevenue() {
//        // 현재 달의 시작일과 종료일을 LocalDateTime으로 변환합니다.
//        LocalDateTime startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay(); // 현재 달의 첫째 날
//        LocalDateTime endDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay(); // 다음 달의 첫째 날
//
//        // 데이터베이스에서 현재 달의 모든 `price` 값을 조회하여 합산합니다.
//        return memberRepository.sumPricesByApplicationDateRange(startDate, endDate);
//
//    }

    //월간 총매출
// 월간 총매출
    // 월간 총매출
    public Map<String, Integer> getMonthlyRegistrationRevenueDetails() {
        Map<String, Integer> revenueDetails = new HashMap<>();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // 현재 달의 첫째 날
        LocalDate endDate = LocalDate.now().plusMonths(1).withDayOfMonth(1); // 다음 달의 첫째 날

        // 각 일자에 대한 매출을 합산하기 위한 변수를 초기화
        int totalMemberRevenue = 0;
        int totalPTRRevenue = 0;
        int totalRestRevenue = 0;
        int totalTransRevenue = 0;

        // 현재 달의 각 날짜에 대해 매출을 계산
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            LocalDateTime localDateTimeStart = date.atStartOfDay(); // 시작 시간
            LocalDateTime localDateTimeEnd = date.plusDays(1).atStartOfDay(); // 다음 날의 시작 시간

            // 해당 날짜의 매출을 계산
            Integer memberRevenue = memberRepository.sumPricesByApplicationDateRange(localDateTimeStart, localDateTimeEnd);
            Integer ptRevenue = ptContractRepository.sumPricesByApplicationDate(localDateTimeStart, localDateTimeEnd);
            Integer restRevenue = restRepository.sumPricesByApplicationDate(localDateTimeStart, localDateTimeEnd);
            Integer transRevenue = trasnferRepository.sumPricesByApplicationDate(localDateTimeStart, localDateTimeEnd);

            // null-safe 처리 및 누적
            totalMemberRevenue += memberRevenue != null ? memberRevenue : 0;
            totalPTRRevenue += ptRevenue != null ? ptRevenue : 0;
            totalRestRevenue += restRevenue != null ? restRevenue : 0;
            totalTransRevenue += transRevenue != null ? transRevenue : 0;
        }

        // 결과를 맵에 추가
        revenueDetails.put("회원권 계약", totalMemberRevenue);
        revenueDetails.put("PT 계약", totalPTRRevenue);
        revenueDetails.put("휴회 계약", totalRestRevenue);
        revenueDetails.put("양도 계약", totalTransRevenue);

        // 총 매출을 계산하여 추가
        int totalMonthlyRevenue = totalMemberRevenue + totalPTRRevenue + totalRestRevenue + totalTransRevenue;
        revenueDetails.put("월 매출", totalMonthlyRevenue);

        // 월간 지출/AS 비용 계산
        Integer monthlyCosts = calculateMonthlyCosts();
        revenueDetails.put("월 지출", monthlyCosts);

        // 월간 환불된 가격 합계 계산
        Integer monthlyRefundedPrices = calculateMonthlyRefundedPrices();
        revenueDetails.put("환불 계약", monthlyRefundedPrices);

        // 최종 월간 총매출 계산 (총 매출 - 월간 비용 - 월간 환불)
        int netMonthlyRevenue = totalMonthlyRevenue - (monthlyCosts != null ? monthlyCosts : 0) - (monthlyRefundedPrices != null ? monthlyRefundedPrices : 0);
        revenueDetails.put("월간 총 매출", netMonthlyRevenue);

        return revenueDetails;
    }




    // 월간 지출/AS 비용
    public Integer calculateMonthlyCosts() {
        // 현재 달의 시작일과 종료일을 LocalDate로 변환합니다.
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // 현재 달의 첫째 날
        LocalDate endDate = LocalDate.now().plusMonths(1).withDayOfMonth(1); // 다음 달의 첫째 날

        // 데이터베이스에서 현재 달의 모든 비용을 조회하여 합산합니다.
        return costRepository.sumPricesByApplicationDateRange(startDate, endDate);
    }

    // 월간 환불된 가격 합계 계산
    public Integer calculateMonthlyRefundedPrices() {
        // 현재 달의 시작일과 종료일을 LocalDateTime으로 변환합니다.
        LocalDateTime startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay(); // 현재 달의 첫째 날 자정
        LocalDateTime endDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay(); // 다음 달의 첫째 날 자정

        // MemberEntity에서 환불된 가격 합계
        Integer memberRefundedPrices = memberRepository.sumRefundedPricesByApplicationDateRange(startDate, endDate);

        // PTContractEntity에서 환불된 가격 합계
        Integer ptRefundedPrices = ptContractRepository.sumRefundedPricesByApplicationDateRange(startDate, endDate);

        // 총 환불된 가격 합계
        return (memberRefundedPrices != null ? memberRefundedPrices : 0) + (ptRefundedPrices != null ? ptRefundedPrices : 0);
    }


    // 마감 정산 PostMapping
    @Transactional
    public void saveFinalReport(List<MemberEntity> todayRegistrations,
                                List<EntryRecordEntity> todayEntries,
                                String money,
                                String revenue,
                                String totalRevenue,
                                MultipartFile[] files) {
        // 오늘의 정산 데이터 생성
        CloserEntity finalize = new CloserEntity();

        // 오늘 등록된 회원 리스트와 출입 기록 설정
        for (MemberEntity member : todayRegistrations) {
            member.setCloserEntity(finalize); // CloserEntity를 소유자로 설정
        }

        // 오늘 등록된 회원 리스트와 출입 기록 설정
        finalize.setMembers(todayRegistrations);
        finalize.setEntryRecords(todayEntries); // List<EntryRecordEntity> 그대로 설정 가능

        finalize.setRevenue(revenue); // 오늘 매출
        finalize.setMoney(money); // 오늘 시재
        finalize.setTotal_revenue(totalRevenue); // 한 달 매출
        finalize.setDateday(LocalDate.now()); // 오늘 날짜

        // 파일 경로를 리스트로 변환하여 설정
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String filePath = "\\src\\main\\resources\\static\\files\\" + fileName; // 파일 경로 설정
            filePaths.add(filePath); // 리스트에 파일 경로 추가
        }

        // CloserEntity에 파일 경로 리스트 설정
        finalize.setFilePath(filePaths.toString()); // 이 메소드는 filePaths 리스트를 저장할 수 있어야 함

        // CloserEntity 저장
        closerRepository.save(finalize);
    }




}
