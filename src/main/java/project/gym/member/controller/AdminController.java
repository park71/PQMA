package project.gym.member.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gym.member.dto.*;
import project.gym.member.entity.*;
import project.gym.member.filter.JwtUtil;
import project.gym.member.repository.LockerRepository;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
@Slf4j
public class AdminController {

    @Autowired
    private JwtUtil jwtUtil;
    private static final String UPLOAD_DIR = "./uploads/";
    @Autowired
    private MemberService memberService;
    @Autowired
    private LockerService lockerService;
    @Autowired
    private BeConsultationService beConsultationService;
    @Autowired
    private EntryRecordService entryRecordService;

    @Autowired
    private RestService restService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private LockerRepository lockerRepository;

        @GetMapping("/adminPage")
        public String adminPage(Model model, HttpSession session) {
            String username = (String) session.getAttribute("loginUsername");
            model.addAttribute("username", username);
            return "adminPage";
        }

//    @GetMapping("/qrcode/{phone}")
//    @ResponseBody
//    public ResponseEntity<Resource> getQRCode(@PathVariable String phone) {
//        try {
//            String path = "C:/qrcode/" + phone + ".png";
//            File file = new File(path);
//
//            if (file.exists()) {
//                Path filePath = Paths.get(file.getAbsolutePath());
//                Resource resource = new UrlResource(filePath.toUri());
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_PNG)
//                        .body(resource);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @GetMapping("/agreement")
    public String agreementPage(Model model){
        model.addAttribute("user", new MemberDTO());
        return "agreement";
    }
    @CrossOrigin(origins = "https://www.priqma.com", allowCredentials = "true")
    @PostMapping("/agreement")
    public String agreementFinish(@ModelAttribute MemberDTO memberDTO, HttpServletRequest request, HttpServletResponse response, Model model) {
//        String signatureData = memberDTO.getSignature(); // Base64 형식의 서명 데이터 가져오기

        // JWT 토큰을 쿠키에서 가져오기
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        if (token != null && jwtUtil.validateToken(token)) {
            // JWT 토큰에서 유저 정보 추출
            String username = jwtUtil.extractUseryd(token);

            // 사용자 정보를 바탕으로 MemberEntity와 MembershipEntity 처리
            memberService.saveMemberWithMembership(memberDTO);
            // 리다이렉트 전 쿠키 재설정 (유효시간 연장 등)
            Cookie newCookie = new Cookie("jwtToken", token);
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return "redirect:/adminPage";
        } else {
            model.addAttribute("errorMessage", "Invalid JWT token");
            return "login";
        }
    }




    @GetMapping("/memberList")
    public String memberListPage(@RequestParam(value = "searchName", required = false) String searchName, Model model) {
        System.out.println("look at the List");

        List<MemberDTO> memberDTOList;
        List<MembershipDTO> membershipDTOList = new ArrayList<>(); // 초기화


        if (searchName != null && !searchName.trim().isEmpty()) {
            // 이름으로 검색하여 해당 회원 조회
            memberDTOList = memberService.findByNaming(searchName); // 이름으로 검색하는 메서드 호출
            System.out.println("Search by name: " + searchName + ", Results: " + memberDTOList.size());


        } else {
            System.out.println("Fetching all members"); // 전체 조회 로직이 타는지 확인
            memberDTOList = memberService.findAll();
//            membershipDTOList = memberService.findAlls(); // 모든 회원을 조회
            System.out.println("Number of members retrieved: " + memberDTOList.size());
        }

        // 모델에 데이터를 추가
        model.addAttribute("memberPage", memberDTOList);
        model.addAttribute("memberships", membershipDTOList);
        return "memberList";
    }

    @GetMapping("/PTinfo")
    public String PTListPage(@RequestParam(value = "searchName", required = false) String searchName, Model model) {
        System.out.println("look at the List");

        List<PTContractDTO> ptContractDTOList;

        if (searchName != null && !searchName.trim().isEmpty()) {
            ptContractDTOList = memberService.findByNamed(searchName); // 이름으로 검색하는 메서드를 서비스에서 호출
            System.out.println("Search by name: " + searchName + ", Results: " + ptContractDTOList.size());

        } else {
            System.out.println("Fetching all members"); // 전체 조회 로직이 타는지 확인

            ptContractDTOList = memberService.findAlli(); // 모든 회원을 조회
            System.out.println("Number of members retrieved: " + ptContractDTOList.size());
        }

        // 모델에 데이터를 추가
        model.addAttribute("ptContract", ptContractDTOList);
        return "PTinfo";
    }

    @GetMapping("/pauseList")
    public String pauseListPage(@RequestParam(value = "searchName", required = false) String searchName, Model model) {
        System.out.println("look at the List");

        List<RestDTO> restDTOList;

        if (searchName != null && !searchName.trim().isEmpty()) {
            restDTOList = memberService.findByNamem(searchName); // 이름으로 검색하는 메서드를 서비스에서 호출
            System.out.println("Search by name: " + searchName + ", Results: " + restDTOList.size());

        } else {
            System.out.println("Fetching all members"); // 전체 조회 로직이 타는지 확인

            restDTOList = memberService.findAllm(); // 모든 회원을 조회
            System.out.println("Number of members retrieved: " + restDTOList.size());
        }

        // 모델에 데이터를 추가
        model.addAttribute("pauseRequests", restDTOList);
        return "pauseList";
    }


    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/lock-check") //계약서 작성 시 락카중복 유무
    public @ResponseBody String lockCheck(@RequestParam("locknum")Integer locknum) {
        System.out.println("locknum =" + locknum);
        String checkResult = memberService.lockCheck(locknum);
        return checkResult;
    }
    @GetMapping("/lockers")
    public String getAllLockers(Model model, HttpSession session) {
        List<LockerEntity> lockers = lockerService.getAllLockers();
        model.addAttribute("lockers", lockers);
        return "lockers";
    }



    @GetMapping("/entry")
    public String entryForm() {
        return "entry";
    }


    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/entry")
    public String registerEntry(@RequestParam("phone") String phoneSuffix, @RequestParam("birth") String birth, Model model) {
        System.out.println("registerEntry 메서드 호출됨");
        boolean isRegistered = memberService.registerEntrys(phoneSuffix, birth);
        if (isRegistered) {
            System.out.println("출입 등록 성공");
            return "entrySuccess"; // 등록 성공 페이지로 이동
        } else {
            model.addAttribute("errorMessage", "존재하지 않는 회원권입니다.");
            return "entry"; // 등록 실패 페이지로 이동
        }
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/entry/from-qr")
    public String registerEntryFromQr(@RequestParam("qrData") String qrData, Model model) {
        System.out.println("QR 코드 데이터: " + qrData);

        // QR 데이터에서 전화번호와 생년월일 추출
        String[] lines = qrData.split("\n");
        String phone = null;
        String birth = null;

        for (String line : lines) {
            if (line.startsWith("전화번호: ")) {
                phone = line.substring("전화번호: ".length()).trim();
                System.out.println("전화번호 추출 성공: " + phone);
            } else if (line.startsWith("생년월일: ")) {
                birth = line.substring("생년월일: ".length()).trim();
                System.out.println("생년월일 추출 성공: " + birth);
            }
        }

        // 전화번호와 생년월일이 모두 추출되었는지 확인
        if (phone != null && birth != null) {
            try {
                // 생년월일을 LocalDate로 변환 (yyyymmdd 형식)
                LocalDate birthDate = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
                // LocalDate를 다시 yyyyMMdd 형식으로 변환
                String formattedBirthDate = birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                boolean isRegistered = memberService.registerEntry(phone, formattedBirthDate); // 변환된 생년월일을 넘김
                System.out.println("등록 결과: " + isRegistered);
                if (isRegistered) {
                    System.out.println("출입 등록 성공");
                    return "entrySuccess"; // 등록 성공 페이지로 이동
                } else {
                    model.addAttribute("errorMessage", "존재하지 않는 회원권입니다.");
                    return "entry"; // 등록 실패 페이지로 이동
                }
            } catch (DateTimeParseException e) {
                model.addAttribute("errorMessage", "생년월일의 형식이 유효하지 않습니다.");
                return "entry"; // 등록 실패 페이지로 이동
            }
        } else {
            model.addAttribute("errorMessage", "QR 코드 데이터가 유효하지 않습니다.");
            return "entry"; // 등록 실패 페이지로 이동
        }
    }



    @GetMapping("/qrcode")
    public String showQrScanner() {
        // templates 디렉토리 안의 qr-scanner.html을 반환
        return "qrcode";
    }




    @GetMapping("/consultationList") //상담리스트 페이지
    public String consultationList(Model model){
        List<ConsultationEntity> consultationEntities = consultationService.findAll();
        List<BeConsultationEntity> beConsultationEntities = beConsultationService.finaAll();
        model.addAttribute("consult", consultationEntities);
        model.addAttribute("beconsult", beConsultationEntities);
        return "consultationList";
    }

    @GetMapping("/transfer") //양도페이지
    public String showTransferForm(HttpSession session, Model model) { // admin계정을 가진 상태에서 진행
        System.out.println("AdminController.showTransferForm");
        return "transfer"; // transfer.html 파일의 이름
    }




    @GetMapping("/members")
    public List<MemberEntity> getAllMembers() {
        return memberService.getAllMembers();
    }
///////////////////////////////////////////////////////////////////게시물 업로드




    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/entries")
    public String getEntriesByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        List<EntryRecordDTO> entries = entryRecordService.getEntriesByDate(date);

        // 시간대별 출입 기록을 분류
        // 시간대별 출입 기록을 분류
        // 시간대별 출입자 수 초기화
        Map<Integer, Long> entryCountsByHourSlot = new HashMap<>();
        for (int hour = 6; hour <= 23; hour++) {
            final int currentHour = hour; // final로 지정
            int count = (int) entries.stream()
                    .filter(entry -> entry.getEntryTime() != null && entry.getEntryTime().getHour() == currentHour)
                    .count();
            entryCountsByHourSlot.put(hour, (long) count);
            System.out.println("Hour: " + hour + ", Count: " + count);
        }


        System.out.println("entryCountsByHourSlot: " + entryCountsByHourSlot);



        // 총 출입자 수 계산
        long totalEntries = entries.size();

        // 모델에 데이터 추가
        model.addAttribute("entries", entries);
        model.addAttribute("entryCountsByHourSlot", entryCountsByHourSlot);
        model.addAttribute("totalEntries", totalEntries);

        return "entrylist";  // entrylist.html로 전달
    }
    @GetMapping("/entrylist")
    public String entrylistPage(Model model){
        return "entrylist";
    }

    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/contract/{id}/decrement")
    public String decrementCount(@PathVariable("id") Long id) {
        memberService.updateCount(id);
        return "redirect:/PTinfo";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/contracts/{id}/decrement")
    public String decrementCounts(@PathVariable("id") Long id) {
        memberService.updateCount(id);
        return "redirect:/userdashboard";
    }

    @GetMapping("/contract/{id}/details")
    public String viewDetails(@PathVariable("id") Long id, Model model) {
        PTContractEntity contract = memberService.getContractById(id);
        List<DecrementRecord> records = memberService.getDecrementRecords(id);
        model.addAttribute("contract", contract);
        model.addAttribute("decrementRecords", records);
        return "decrement";
    }
    @GetMapping("/fixed/{id}/details")
    public String fixedDetails(@PathVariable("id") Long id, Model model) {
        // 회원 정보를 Optional로 가져옵니다.
        Optional<PTContractEntity> memberOptional = memberService.findByIds(id);

        // Optional에서 실제 객체를 가져와 모델에 추가합니다.
        if (memberOptional.isPresent()) {
            PTContractEntity member = memberOptional.get();
            model.addAttribute("member", member);
        } else {
            // 해당 ID의 회원 정보가 없을 경우 처리 (예: 에러 페이지로 이동)
            return "redirect:/errorPage"; // 적절한 에러 처리 페이지로 리다이렉트
        }

        return "PTinfofix";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/edits/{id}")
    public String editMember(@PathVariable("id") Long id, @ModelAttribute("member") PTContractEntity updatedMember) {
        // 기존 회원 정보를 가져옵니다.
        Optional<PTContractEntity> existingMember = memberService.findByIds(id);

        // 회원 정보가 존재하는지 확인합니다.
        if (existingMember.isPresent()) {
            PTContractEntity existingMembers = existingMember.get();

            // 기존 정보를 수정합니다.
            existingMembers.setName(updatedMember.getName());
            existingMembers.setAddress(updatedMember.getAddress());
            existingMembers.setCoach(updatedMember.getCoach());
            existingMembers.setPhone(updatedMember.getPhone());
            existingMembers.setGender(updatedMember.getGender());
            existingMembers.setKakao(updatedMember.getKakao());
            existingMembers.setBirth(updatedMember.getBirth());
            existingMembers.setPurpose(updatedMember.getPurpose());
            existingMembers.setPtstart(updatedMember.getPtstart());
            existingMembers.setPtmembership(updatedMember.getPtmembership());
            existingMembers.setCount(updatedMember.getCount());
            existingMembers.setCredit(updatedMember.getCredit());
            existingMembers.setPrice(updatedMember.getPrice());


            // 필요한 다른 필드들도 위와 같이 수정할 수 있습니다.

            // 변경된 정보를 저장합니다.
            // 변경된 정보를 저장합니다.
            memberService.saves(existingMembers);

            // 수정 후 리다이렉트할 경로를 지정합니다.
            return "redirect:/PTinfo"; // 수정이 완료된 후 이동할 페이지 경로를 지정합니다.
        }else {
            // 회원 정보를 찾을 수 없는 경우 처리 (예: 에러 페이지로 이동)
            return "redirect:/errorPage"; // 에러 처리 경로를 지정합니다.
        }
    }



    @GetMapping("/PTagreement")
    public String ptagreementPage(Model model){
        model.addAttribute("user", new MemberDTO());
        return "PTagreement";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/PTagreement")
    public String ptagreement(@ModelAttribute PTContractDTO ptContractDTO, HttpServletRequest request, HttpServletResponse response, Model model){
        // JWT 토큰을 쿠키에서 가져오기
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            // JWT 토큰에서 유저 정보 추출
            String username = jwtUtil.extractUseryd(token);

            // 사용자 정보를 바탕으로 MemberEntity 생성
            PTContractEntity ptuser = new PTContractEntity();
            ptuser.setPhone(ptContractDTO.getPhone());
            ptuser.setAddress(ptContractDTO.getAddress());
            ptuser.setBirth(ptContractDTO.getBirth());
            ptuser.setCoach(ptContractDTO.getCoach());
            ptuser.setCredit(ptContractDTO.getCredit());
            ptuser.setGender(ptContractDTO.getGender());
            ptuser.setName(ptContractDTO.getName());
            ptuser.setKakao(ptContractDTO.getKakao());
            ptuser.setPurpose(ptContractDTO.getPurpose());
            ptuser.setPtmembership(ptContractDTO.getPtmembership());
            ptuser.setPtstart(ptContractDTO.getPtstart());
            ptuser.setCount(Integer.valueOf(ptContractDTO.getCount()));
            ptuser.setStatus(ptContractDTO.getStatus());
            ptuser.setApplicationDate(ptContractDTO.getApplicationDate());
            ptuser.setPrice(ptContractDTO.getPrice());
            ptuser.setSignature(ptContractDTO.getSignature());
            // 사용자 정보 저장

            ptuser.setStatus("approved");
            String phone = ptuser.getPhone();
            String kakao = "만리" +ptuser.getName() + phone.substring(phone.length() - 4);
            ptuser.setKakao(kakao);
            memberService.PTregisterOrUpdateUser(ptuser);
            System.out.println("계약서 작성완료");
            // 디버깅 로그 추가
            log.info("MemberDTO phone: {}", ptContractDTO.getPhone());
            log.info("MemberDTO name: {}", ptContractDTO.getName());

            memberService.PTmemberProcess(ptContractDTO); // 여기에 계약서 값 저장
            // 리다이렉트 전 쿠키 재설정 (유효시간 연장 등)
            Cookie newCookie = new Cookie("jwtToken", token);
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return "redirect:/adminPage";
        } else {
            model.addAttribute("errorMessage", "Invalid JWT token");
            return "login";
        }
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/api/lockers/collect")
    public String collectLocker(@RequestParam Long lockerId) {
        lockerService.collectLocker(lockerId);

        return "redirect:/lockers";
    }
    @GetMapping("/lockout")
    public String LockerOut(Model model) {
        // 모든 락커 리스트를 가져옵니다.
        List<LockHistoryEntity> allLocker = lockerService.getAllLocker();
        model.addAttribute("lockers", allLocker); // 모든 락커 리스트를 Model에 추가합니다.
        return "lockout"; // lockout.html로 이동
    }
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam Long lockerId , @RequestParam String status) {
        LockHistoryEntity lockers = lockerService.findByIds(lockerId);
        if (lockers != null) {
            lockers.setStatus(status); // 상태 업데이트
            lockerService.saves(lockers); // 변경사항 저장
        }
        return "redirect:/lockout"; // 업데이트 후 락커 리스트 페이지로 리다이렉트
    }

    @GetMapping("/longtime")
    public String getLongAbsentMembers(Model model) {
        List<MemberEntity> longAbsentMembers = memberService.getLongAbsentMembers();

        model.addAttribute("members", longAbsentMembers);
        return "longtime"; // 해당 HTML 파일 이름
    }
    @PostMapping("/longabsent")
    @ResponseBody
    public ResponseEntity<?> handleLongAbsent(
            @RequestParam("memberId") String memberId,
            @RequestParam("ring") String callStatus,
            @RequestParam("longTime") String longTime) {

        System.out.println("멤버아이디: " + memberId);
        System.out.println("전화유무: " + callStatus);
        System.out.println("추가사항: " + longTime);

        try {
            Integer id = Integer.parseInt(memberId);
            MemberEntity member = memberService.findByIda(Long.valueOf(id)).orElse(null);

            if (member != null) {
                member.setRing(callStatus);
                member.setLongTime(longTime);
                memberService.save(member);
                // 저장 후 longTime 값을 포함하여 응답
                return ResponseEntity.ok(Map.of("message", "회원 정보가 업데이트되었습니다.", "newLongTime", longTime));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("잘못된 ID 형식입니다.");
        }

        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }




    @GetMapping("/lastday")
    public String LastPeople(Model model){
        List<MemberEntity> lastpersonal = memberService.getLastDay();
        model.addAttribute("members",lastpersonal);
        return "lastday";
    }



}







