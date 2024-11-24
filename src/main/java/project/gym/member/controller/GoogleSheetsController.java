package project.gym.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.gym.member.service.GoogleSheetsService;
import project.gym.member.service.MemberService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class GoogleSheetsController {

    @Autowired
    private GoogleSheetsService googleSheetsService;
    @Autowired
    private MemberService memberService;

    public GoogleSheetsController(GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    @GetMapping("/sheets/read")
    public List<List<Object>> readData(@RequestParam(defaultValue = "A1:C3") String range) {
        try {
            return googleSheetsService.readSheetData(range);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to read data from Google Sheets", e);
        }
    }

    @GetMapping("/sync-sheets")
    public ResponseEntity<String> syncData() {
        try {
            googleSheetsService.syncDataToSheets();
            return ResponseEntity.ok("Data synchronized successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to sync data: " + e.getMessage());
        }
    }



    @GetMapping("/searchMember")
    public ResponseEntity<List<Map<String, Object>>> searchMember(@RequestParam("name") String name) {
        try {
            List<Map<String, Object>> memberList = googleSheetsService.findMembersByNames(name);
            System.out.println("이름값"+name);
            if (!memberList.isEmpty()) {
                System.out.println("여기맞지");
                return new ResponseEntity<>(memberList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);  // 빈 배열 반환
            }
        } catch (Exception e) {
            e.printStackTrace();  // 에러 로그 출력
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 서버 오류 처리
        }
    }




    // 회원 데이터 가져오기
    @PostMapping("/importMember")
    public String importMember(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String phone = request.get("phone");

        // name과 phone이 null인지 확인
        if (name == null || name.trim().isEmpty()) {
            return "이름이 비어있습니다.";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "전화번호가 비어있습니다.";
        }

        try {
            List<Map<String, Object>> memberData = googleSheetsService.findMembersByName(name);
            System.out.println("구글 시트에서 가져온 데이터: " + memberData);

            if (memberData != null) {
                memberService.insertMember(memberData);
                return "회원 정보가 성공적으로 가져와졌습니다.";
            } else {
                return "해당 전화번호로 회원을 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다.";
        }
    }
}