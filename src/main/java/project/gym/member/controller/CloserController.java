package project.gym.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.entity.CloserEntity;
import project.gym.member.entity.EntryRecordEntity;
import project.gym.member.entity.MemberEntity;
import project.gym.member.entity.PTContractEntity;
import project.gym.member.repository.CloserRepository;
import project.gym.member.service.CloserService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Controller
public class CloserController {

    @Autowired
    private CloserService closerService;

    @Autowired
    private CloserRepository closerRepository;
    @GetMapping("/getRevenueByDate")
    @ResponseBody
    public List<CloserEntity> getRevenueByDate(@RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date); // 날짜 파싱
        return closerService.getRevenueByDates(localDate); // 서비스 호출하여 데이터 가져오기
    }
    @GetMapping("/getCalendarData")
    @ResponseBody
    public Map<String, Object> getCalendarData(@RequestParam int year, @RequestParam int month) {
        Map<String, Object> response = new HashMap<>();
        response.put("year", year);
        response.put("month", month + 1); // 0-11형태를 1-12형태로 변경

        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);

        int lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDate; day++) {
            days.add(String.format("%d-%02d-%02d", year, month + 1, day));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        response.put("days", days);
        return response;
    }

    @GetMapping("/closer")
    public String CloerTime(Model model) {
        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();

        // 현재 연도와 월 설정
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0-11 형태로 반환

        // 전체 달의 날짜 목록 생성
        List<Date> calendarDays = new ArrayList<>();
        Map<String, Integer> dateRevenueMap = new HashMap<>(); // 날짜별 revenue 데이터를 저장할 Map

        // 해당 월의 첫 번째 날로 설정
        calendar.set(year, month, 1);

        // 첫 번째 날의 요일을 찾음
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 이전 빈 칸 추가
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDays.add(null); // 빈 칸 추가
        }

        // 해당 월의 모든 날짜 추가 및 revenue 조회
        int lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDate; day++) {
            Date currentDate = calendar.getTime();
            calendarDays.add(currentDate);

            // Format the current date to match the database format "YYYY-MM-DD"
            String dateString = String.format("%d-%02d-%02d", year, month + 1, day);

            // String을 LocalDate로 변환
            LocalDate localDate = LocalDate.parse(dateString);
            Integer revenue = closerService.getRevenueByDate(localDate); // LocalDate를 사용하여 revenue 조회
            dateRevenueMap.put(dateString, revenue != null ? revenue : 0); // null일 경우 0으로 설정

            System.out.println("Formatted Date: " + dateString); // 로그를 통해 확인
            // 다음 날짜로 이동
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        // 모델에 데이터 추가
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("dateRevenueMap", dateRevenueMap);



//        //캘린더기능
//        model.addAttribute("calendarDays", calendarDays);

        model.addAttribute("cardPrices", closerService.getPriceByCreditTypeAndToday("카드"));
        model.addAttribute("cashPrices", closerService.getPriceByCreditTypeAndToday("현금"));
        model.addAttribute("transferPrices", closerService.getPriceByCreditTypeAndToday("계좌이체"));

        // 오늘의 출입 기록 목록 추가
        List<EntryRecordEntity> todayAttendance = closerService.getTodayAttendance();
        model.addAttribute("todayAttendance", todayAttendance);
        // 템플릿에 오늘의 출입 기록을 전달하여 오늘 출입한 회원의 정보 표시

        // 오늘의 신규 회원 등록 목록 추가
        List<MemberEntity> todayRegistrations = closerService.getTodayRegistrations();
        model.addAttribute("todayRegistrations", todayRegistrations);
        // 템플릿에 오늘 가입한 회원 목록을 전달하여 당일 신규 등록 회원 정보 표시

        //오늘의 신규 PT 계약
        List<PTContractEntity> todayRegistration = closerService.getTodayRegistration();
        model.addAttribute("todayRegistration", todayRegistration);
//

        Map<String, Integer> revenueDetails = closerService.getTodayRegistrationRevenueDetails();

        model.addAttribute("todayRevenue", revenueDetails.get("todayRevenue"));
        model.addAttribute("memberRevenue", revenueDetails.get("memberRevenue"));
        model.addAttribute("ptRevenue", revenueDetails.get("ptRevenue"));
        model.addAttribute("restRevenue", revenueDetails.get("restRevenue"));
        model.addAttribute("transRevenue", revenueDetails.get("transRevenue"));


        System.out.println("calendarDays: " + calendarDays);
        System.out.println("todayRevenue: " + revenueDetails);
        // 템플릿에 오늘 날짜의 매출 합계 전달하여 오늘 발생한 매출액 표시

        // 시간대별 출입 횟수 집계 추가
        Map<String, Long> entryCountsByHourSlot = closerService.getAttendanceCountsByHour();
        model.addAttribute("entryCountsByHourSlot", entryCountsByHourSlot);
        // 템플릿에 시간대별 출입 횟수 전달하여 출입이 많은 시간대 확인 및 분석에 활용


        // 주간 일별 매출 합계 데이터 추가
        Map<String, Integer> weeklyRegistrationRevenueMap = closerService.calculateWeeklyRegistrationRevenue();
        model.addAttribute("weeklyRegistrationRevenueMap", weeklyRegistrationRevenueMap);
        // 템플릿에 주간 일별 매출 데이터를 전달하여 일주일간의 매출 기록을 일별로 표시

        // 월간 총 매출 데이터 추가
        // 월간 총 매출 데이터 추가
        Map<String, Integer> monthlyRevenue = closerService.getMonthlyRegistrationRevenueDetails();
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        // 템플릿에 이번 달 총 매출 합계를 전달하여 월별 매출 현황 확인 가능



        return "closer";
    }



    @PostMapping("/finalize")
    public String finalizeReport(@RequestParam("money") String money,
                                 @RequestParam("revenue") String revenue,
                                 @RequestParam("totalRevenue") String totalRevenue,
                                 @RequestParam("files") MultipartFile[] files, // 여러 파일을 위한 배열
                                 Model model) {

        // 오늘 등록한 회원 리스트를 가져옵니다.
        List<MemberEntity> todayRegistrations = closerService.getTodayRegistrations();

        // 오늘 출입 기록을 가져옵니다.
        List<EntryRecordEntity> todayEntries = closerService.getTodayAttendance();


        String filePath = "C:\\Users\\PC\\Park study\\gym\\src\\main\\resources\\static\\files\\";

        // 파일 저장 로직
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                // 파일을 저장할 전체 경로
                File destinationFile = new File(filePath + fileName);
                file.transferTo(destinationFile); // 파일 저장
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 저장에 실패했습니다.");
            return "redirect:/adminPage"; // 오류 발생 시 리다이렉트
        }

        // 서비스 호출하여 매출과 파일 데이터 저장
        closerService.saveFinalReport(todayRegistrations, todayEntries, money, revenue, totalRevenue, files);

        model.addAttribute("message", "정산이 완료되었습니다!");
        return "redirect:/adminPage"; // 완료 후 리다이렉트
    }

    }




