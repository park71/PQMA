package project.gym.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.repository.InbodyRepository;
import project.gym.member.service.InbodyFileService;
import project.gym.member.service.MemberService;

import java.util.Map;

@RestController
@RequestMapping("/inbodys")
public class InbodyWebhookController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private InbodyFileService inbodyFileService;
    @Autowired
    private InbodyRepository inbodyRepository;

    private static final String INBODY_API_URL = "https://api.inbody.com/v1/testdata"; // InBody API URL
    private static final String INBODY_API_KEY = "rVVcGg4bTVaf9Eg8BeYJZONW4ZrI3bZtEM65WFcElNc="; // API Key
    private static final String INBODY_LOCATION_ID = "10002gym"; // Location ID

    // Webhook 데이터를 처리하는 엔드포인트
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> webhookData,
                                                @RequestHeader("X-Integration-Key") String integrationKey,
                                                @RequestHeader("X-Location-ID") String locationId) {
        // API 키 및 Location ID 검증
        if (!integrationKey.equals(INBODY_API_KEY) || !locationId.equals(INBODY_LOCATION_ID)) {
            return new ResponseEntity<>("Unauthorized: Invalid integration key or location ID", HttpStatus.UNAUTHORIZED);
        }

        // Webhook 데이터가 유효한지 확인
        if (webhookData == null || webhookData.isEmpty()) {
            return new ResponseEntity<>("No data received", HttpStatus.BAD_REQUEST);
        }

        // TempData 처리 (필요한 경우 데이터 수정 또는 삭제)
        boolean isTempData = "true".equalsIgnoreCase((String) webhookData.get("IsTempData"));
        if (isTempData) {
            // TempData 검토 필요 로직 (예: 데이터 수정 또는 삭제)
            return new ResponseEntity<>("Data flagged as TempData, needs review", HttpStatus.OK);
        }

        try {
            // InBody API로 데이터를 전송
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", INBODY_API_KEY);  // API Key를 Authorization 헤더로 추가
            headers.set("X-Location-ID", INBODY_LOCATION_ID); // Location ID 헤더 추가

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(webhookData, headers);
            // Webhook 데이터 전송
            String response = restTemplate.postForObject(INBODY_API_URL, entity, String.class);

            return new ResponseEntity<>("Webhook data successfully processed: " + response, HttpStatus.OK);

        } catch (Exception e) {
            // 예외 발생 시 에러 처리
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//        // 테스트용 Webhook 엔드포인트
//        @GetMapping("/test")
//        public ResponseEntity<String> testWebhook() {
//            return new ResponseEntity<>("Test webhook is successful", HttpStatus.OK);
//        }


    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/upload")
    public void receiveInbodyData(@RequestParam("name") String name,
                                  @RequestParam("birth") String birth,
                                  @RequestParam("imagePath")MultipartFile image) {

        // MemberEntity에 inbody 필드 업데이트
        inbodyFileService.saveInbodyData(name, birth, image);
    }
}



//방안1
//@PostMapping("/webhook")
//public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> webhookData,
//                                            @RequestHeader("X-Integration-Key") String integrationKey,
//                                            @RequestHeader("X-Location-ID") String locationId) {
//    // API 키 및 Location ID 검증
//    if (!integrationKey.equals(INBODY_API_KEY) || !locationId.equals(INBODY_LOCATION_ID)) {
//        return new ResponseEntity<>("Unauthorized: Invalid integration key or location ID", HttpStatus.UNAUTHORIZED);
//    }
//
//    // Webhook 데이터가 유효한지 확인
//    if (webhookData == null || webhookData.isEmpty()) {
//        return new ResponseEntity<>("No data received", HttpStatus.BAD_REQUEST);
//    }
//
//    try {
//        // 이미지 URL 추출
//        String imageUrl = (String) webhookData.get("ImageUrl");
//        String phoneNumber = (String) webhookData.get("PhoneNumber");
//
//        if (imageUrl != null && phoneNumber != null) {
//            // 이미지 다운로드 및 저장
//            URL url = new URL(imageUrl);
//            String filename = "inbody_" + phoneNumber + ".jpg";
//            Path filePath = Paths.get("./resource/inbody", filename);
//
//            try (InputStream in = url.openStream()) {
//                Files.createDirectories(filePath.getParent()); // 폴더가 없으면 생성
//                FileCopyUtils.copy(in, Files.newOutputStream(filePath));
//            }
//
//            // DB에 파일 경로 저장
//            InbodyFileEntity inbodyFile = new InbodyFileEntity();
//            inbodyFile.setPhoneNumber(phoneNumber);
//            inbodyFile.setFilePath(filePath.toString());
//            inbodyFileService.saveInbodyFile(inbodyFile); // DB에 저장
//
//            return new ResponseEntity<>("Image successfully saved", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Image URL or phone number missing", HttpStatus.BAD_REQUEST);
//        }
//
//    } catch (Exception e) {
//        // 예외 발생 시 에러 처리
//        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}

//방안2
//@PostMapping("/webhook")
//public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> webhookData,
//                                            @RequestHeader("X-Integration-Key") String integrationKey,
//                                            @RequestHeader("X-Location-ID") String locationId) {
//    if (!integrationKey.equals(INBODY_API_KEY) || !locationId.equals(INBODY_LOCATION_ID)) {
//        return new ResponseEntity<>("Unauthorized: Invalid integration key or location ID", HttpStatus.UNAUTHORIZED);
//    }
//
//    if (webhookData == null || webhookData.isEmpty()) {
//        return new ResponseEntity<>("No data received", HttpStatus.BAD_REQUEST);
//    }
//
//    boolean isTempData = "true".equalsIgnoreCase((String) webhookData.get("IsTempData"));
//    if (isTempData) {
//        return new ResponseEntity<>("Data flagged as TempData, needs review", HttpStatus.OK);
//    }
//
//    try {
//        String phoneNumber = (String) webhookData.get("PhoneNumber");
//        String imageUrl = (String) webhookData.get("ResultImageUrl");
//
//        if (imageUrl == null || imageUrl.isEmpty()) {
//            return new ResponseEntity<>("No image URL provided", HttpStatus.BAD_REQUEST);
//        }
//
//        // 이미지 다운로드
//        RestTemplate restTemplate = new RestTemplate();
//        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
//
//        if (imageBytes == null || imageBytes.length == 0) {
//            return new ResponseEntity<>("Failed to download image", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        // 파일 저장 경로 설정
//        String filename = "inbody_" + phoneNumber + ".jpg";
//        Path filePath = Paths.get("./resource/inbody", filename);
//
//        // 디렉토리가 없으면 생성
//        Files.createDirectories(filePath.getParent());
//
//        // 이미지 파일 저장
//        Files.write(filePath, imageBytes);
//
//        // 데이터베이스에 저장
//        InbodyFileEntity inbodyFile = new InbodyFileEntity();
//        inbodyFile.setPhoneNumber(phoneNumber);
//        inbodyFile.setFilePath(filePath.toString());
//        inbodyFileService.saveInbodyFile(inbodyFile);
//
//        return new ResponseEntity<>("Image successfully saved", HttpStatus.OK);
//
//    } catch (Exception e) {
//        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}





//
//    @PostMapping("/webhook")
//    public ResponseEntity<String> receiveWebhook(
//            @RequestBody Map<String, Object> payload,
//            @RequestHeader("X-Integration-Key") String integrationKey,
//            @RequestHeader("X-Location-ID") String locationId) {
//
//        System.out.println("X-Integration-Key: " + integrationKey);
//        System.out.println("X-Location-ID: " + locationId);
//
//        if (!"rVVcGg4bTVaf9Eg8BeYJZONW4ZrI3bZtEM65WFcElNc=".equals(integrationKey)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid IntegrationKey");
//        }
//
//        if (!"10002gym".equals(locationId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Location Id");
//        }
//
////        // 전화번호로 회원을 조회하는 예시
////        String phoneNumber = (String) payload.get("phoneNumber"); // 인바디 데이터에서 전화번호 받기
////
////        Optional<MemberEntity> member = memberService.findByPhoneNumbera(phoneNumber); // 전화번호로 회원 조회
////
////        if (!member.isPresent()) {
////            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
////        }
////// Inbody 데이터 처리 로직
////        String memberName = member.get().getName();
////        String pdfUrl = (String) payload.get("pdfUrl");
//// 전화번호를 포함하여 고유한 파일명 생성
////        inbodyFileService.saveFile(pdfUrl, phoneNumber + "-inbody.pdf");
//        return ResponseEntity.ok("Webhook received successfully");
//
//    }
//
//    @GetMapping("/member/inbody/{memberId}")
//    public String viewInbodyRecords(@PathVariable Long memberId, Model model) {
//        // 회원 ID로 회원 정보 조회
//        MemberEntity member = memberService.findByIdif(memberId);
//
//        // 회원의 이름으로 인바디 파일 조회
//        List<InbodyEntity> inbodyFiles = inbodyFileService.findInbodyFilesByMemberName(member.getName());
//
//        // 모델에 추가
//        model.addAttribute("inbodyFiles", inbodyFiles);
//        model.addAttribute("memberName", member.getName());
//
//        return "inbodyFiles"; // inbodyFiles.html로 반환
//    }

//}

//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleWebhook(
//            @RequestParam("name") String name,
//            @RequestParam("file") MultipartFile file) {
//
//        try {
//            // 회원 이름으로 DB에서 회원 정보 조회
//            MemberEntity member = memberService.findAllByNames(name);
//            if (member == null) {
//                return new ResponseEntity<>("회원 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
//            }
//
//            // 파일 저장 경로 설정
//            String fileName = name + "_inbody.pdf";
//            String filePath = "/uploads/inbody/" + fileName;
//            File destFile = new File(filePath);
//
//            // PDF 파일 저장
//            file.transferTo(destFile);
//
//            // DB에 파일 경로 저장
//            InbodyEntity inbodyFile = new InbodyEntity();
//            inbodyFile.setMember(member);
//            inbodyFile.setFile_path(filePath);
//            inbodyFile.setRecordDate(re);
//            inbodyFileService.savefFile(inbodyFile);
//
//            return new ResponseEntity<>("InBody PDF 파일이 성공적으로 저장되었습니다.", HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        @PostMapping("/webhook")
//        public ResponseEntity<String> receiveInbodyData(@RequestBody Map<String, Object> payload) {
//            // 요청 데이터 검증 (예: InBody에서 전송된 secret key 검증)
//            String secretKey = (String) payload.get("secretKey");
//            if (!"your-secret-key".equals(secretKey)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid secret key");
//            }
//
//            // 요청 데이터 파싱
//            String memberName = (String) payload.get("name");
//            String pdfUrl = (String) payload.get("pdfUrl");
//            LocalDate recordDate = LocalDate.parse((String) payload.get("recordDate"));
//
//            // 데이터베이스에 저장할 InbodyEntity 생성
//            InbodyEntity inbodyRecord = new InbodyEntity();
//            inbodyRecord.setName(memberName);
//            inbodyRecord.setFile_path(pdfUrl);
//            inbodyRecord.setRecordDate(recordDate);
//            inbodyRecord.setUpload_time(LocalDateTime.now());
//
//            // 회원 정보 조회 및 연결
//            MemberEntity member = memberService.findAllByNames(memberName);
//            if (member != null) {
//                inbodyRecord.setMember(member);
//            }
//
//            // 데이터 저장
//            inbodyRepository.save(inbodyRecord);
//
//            return ResponseEntity.ok("InBody data received and processed successfully");
//        }