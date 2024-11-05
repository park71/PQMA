package project.gym.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.gym.member.service.JoinService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private JoinService joinService;

    // ID 중복검사 API
    @GetMapping("/check-duplicate-id")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestParam("useryd") String useryd) {
        boolean exists = joinService.isUserydTaken(useryd);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}