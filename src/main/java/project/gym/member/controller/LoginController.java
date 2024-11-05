package project.gym.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.gym.member.dto.*;
import project.gym.member.entity.BoardEntity;
import project.gym.member.entity.ConsultationEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.filter.JwtUtil;
import project.gym.member.repository.ConsultationRepository;
import project.gym.member.repository.EntryRecordRepository;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j

public class LoginController {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JoinService joinService;
    @Autowired
    private EntryRecordRepository entryRecordRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private EntryRecordService entryRecordService;
    @Autowired
    private LockerService lockerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private BeConsultationService beConsultationService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationResponse authenticationResponse;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @GetMapping("/login")
    public String loginPage() {
        System.out.println("login page");
        return "login"; // 뷰 이름을 반환 (Thymeleaf를 사용할 경우
    }
    @Autowired
    public LoginController(JoinService joinService,
                           @Qualifier("authenticationManager")AuthenticationManager authenticationManager,
                           CustomUserDetailService userDetailsService, JwtUtil jwtUtil) {
        this.joinService = joinService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/resetPassword")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        System.out.println("Received token in GET: " + token);
        Optional<UserEntity> optionalUser = joinService.findByResetToken(token);
        if (optionalUser.isPresent()) {
            model.addAttribute("token", token);
            return "resetPassword"; // 비밀번호 재설정 폼 페이지
        } else {
            model.addAttribute("errorMessage", "유효하지 않은 토큰입니다.");
            return "error"; // 에러 페이지로 이동
        }
    }

    @CrossOrigin(origins = "https://www.priqma.com") // 특정 출처 허용
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        System.out.println("Received token: " + token);
        Optional<UserEntity> optionalUser = joinService.findByResetToken(token);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            System.out.println("User found: " + user.getUsername());
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 재설정되었습니다.");
            return "redirect:/login";
        } else {
            System.out.println("Invalid token: " + token);
            redirectAttributes.addFlashAttribute("error", "유효하지 않은 토큰입니다.");
            return "redirect:/login";
        }
    }

    @PostMapping("/forgotPassword")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        Optional<UserEntity> optionalUser = joinService.findByEmail(email); // 이메일로 사용자 검색

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            // 임시 토큰 생성 및 DB에 저장
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            joinService.save(user);

            // 비밀번호 재설정 링크 생성
            String resetLink = "http://priqma.com/resetPassword?token=" + resetToken;

            // 이메일 전송
            joinService.sendPasswordResetEmail(email, resetLink);
            response.put("success", "true");
            response.put("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        } else {
            response.put("success", "false");
            response.put("message", "해당 이메일로 가입된 계정이 없습니다.");
        }

        return ResponseEntity.ok(response); // JSON 응답
    }

    @CrossOrigin(origins = "https://www.priqma.com") // 특정 출처 허용
    @PostMapping("/loginProc")
    public String loginProc(@RequestParam String useryd, @RequestParam String password,
                            HttpSession session, HttpServletResponse response, Model model) {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setUsername(useryd);
        joinDTO.setPassword(password);
        System.out.println("login 진입");

        // 데이터베이스에서 사용자 찾기
        Optional<UserEntity> optionalUser = joinService.findByUseryd(useryd);


        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            // 비밀번호 비교
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 로그인 성공 처리
                session.setAttribute("loginUsername", user.getUsername());
                session.setAttribute("loginUseryd", user.getUseryd());
            }

            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(useryd, password) // username 대신 useryd 사용
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                UserDetails customUserDetails = userDetailsService.loadUserByUsername(useryd); // useryd로 변경
                String jwtToken = jwtUtil.generateToken(customUserDetails);


                log.info("생성된 JWT 토큰값: {}", jwtToken);

                Cookie cookie = new Cookie("jwtToken", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
                cookie.setPath("/");
                response.addCookie(cookie);

                // Check user role and redirect accordingly
                if (customUserDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    return "redirect:/adminPage";
                } else {
                    System.out.println("로그인하는 회원이름"+joinDTO.getUsername());
                    System.out.println("로그인은 성공한거야");
                    return "redirect:/";
                }

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "비밀번호를 확인해주세요!: " + e.getMessage());
                return "login";
            }
        } else {
            model.addAttribute("errorMessage", "로그인 실패!");
            return "login";
        }
    }






    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();

        return "home";
    }

    @GetMapping("/list")//홈페이지 회원 리스트
    public String findAll(HttpSession session,Model model) {
        System.out.println("hi");
        List<JoinDTO> memberDTOList = joinService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

   @GetMapping("/info")
   public String updateForm(HttpSession session, Model model) {
       String useryd = (String) session.getAttribute("loginUseryd");

       // 로그인 상태 체크
       if (useryd == null) {
           return "redirect:/login"; // 로그인 페이지로 리다이렉트
       }

       JoinDTO joinDTO = joinService.updateForm(useryd);
       model.addAttribute("user", joinDTO);
       String username = (String) session.getAttribute("loginUsername");

       // 기본값으로 loggedIn 변수를 false로 설정
       model.addAttribute("loggedIn", false);
       model.addAttribute("loginUseryd", false);

       if (useryd != null) {
           model.addAttribute("loggedIn", true);
           model.addAttribute("username", username);
           model.addAttribute("loginUseryd", true);

           // 사용자 정보를 조회해서 추가
           UserEntity user = userRepository.findByUseryd(useryd);
           if (user != null) {
               model.addAttribute("user", user);
           } else {
               model.addAttribute("error", "User not found");
           }
       }
       return "info";
   }

   @GetMapping("/locker")
    public String lockerList(Model model) {

       return "locker";
   }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 현재 인증된 사용자의 정보를 가져옴
        String name = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");
        // 디버깅 로그 추가
        System.out.println("Logged in user: " + name);
        System.out.println("Logged in userid: " +useryd);

        // 사용자 이름으로 회원 정보를 가져옴
        if (useryd != null) {
            // 사용자 이름으로 회원 정보를 가져옴
            MemberDTO memberDTO = memberService.findByName(name);
            System.out.println("user"+name);
            if (memberDTO != null) {
                model.addAttribute("member", memberDTO);

                // 사용자 이름으로 출입 기록을 가져옴
                List<EntryRecordDTO> entryRecordDTOs = entryRecordService.findByMemberName(name);
                model.addAttribute("entryRecords", entryRecordDTOs);

                LockerDTO lockerDTO = lockerService.findByName(name);
                model.addAttribute("lockers", lockerDTO);
                System.out.println("정보 입력 완료");
            } else {
                // 회원 정보를 찾을 수 없는 경우
                model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
            }
        } else {
            // 세션에서 로그인 사용자 이름이 없는 경우 처리
            model.addAttribute("error", "로그인 정보를 가져올 수 없습니다.");
        }

        return "dashboard";
    }
    @GetMapping("/consultationForm")
    public String showConsultationForm(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loginUsername");
        System.out.println(username);
        if (username == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        UserEntity user = userRepository.findByUsername(username);
        System.out.println(user);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("loggedIn", true); // loggedIn 상태 추가
            model.addAttribute("consultationForm", new ConsultationEntity());
            return "consultationForm";
        } else {
            model.addAttribute("error", "User not found");
            return "home";
        }
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/consultation")
    public String submitConsultation(@ModelAttribute("consultationForm")ConsultationEntity consultationEntity,
                                     HttpSession session, Model model) {
        String username = (String) session.getAttribute("loginUsername");
        if (username == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        UserEntity user = userRepository.findByUsername(username);

        if (user != null) {
            consultationEntity.setUser(user);
            // UserEntity로부터 값을 설정
            consultationEntity.setUser(user);
            consultationEntity.setUsername(user.getUsername());
            consultationEntity.setEmail(user.getEmail());
            consultationEntity.setPhnum(user.getPhnum());

            // Check if consultation already exists for the same time
            LocalDateTime sangdamTime = consultationEntity.getSangdamTime();
            System.out.println(sangdamTime);
            if (consultationService.checkDuplicateConsultation(sangdamTime)) {
                model.addAttribute("error", "이미 예약이 존재합니다. 다른 시간대를 선택해주세요.");
                return "error";
            }
            // 상담 엔터티 저장 로직 추가
            consultationRepository.save(consultationEntity);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }
    @GetMapping("/becon")
    public String showConsultationForm(Model model) {
        model.addAttribute("beconsultForm", new BeConsultDTO());
        return "becon";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/beconsult")
    public String submitConsultation(@ModelAttribute("beconsultForm") BeConsultDTO guestConsultationDTO, Model model) {

        beConsultationService.saveConsultation(guestConsultationDTO);
        System.out.println(guestConsultationDTO);
        return "redirect:/";
    }


    @GetMapping("/event")
    public String eventPage(Model model,HttpSession session,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");



        Page<BoardEntity> list = null;


        if(searchKeyword == null) {
            list = memberService.boardList(pageable);
        }else {
            list = memberService.boardSearchList(searchKeyword, pageable);
        }


        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        //model.addAttribute("imageUrls",imageUrls);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("loginUseryd", false);

        if (username != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginUseryd",true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }


        return "event";

    }
    @GetMapping("/introduce")
    public String introducePage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);

        String useryd = (String) session.getAttribute("loginUseryd");
        model.addAttribute("loginUseryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("loginUseryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }

        return "introduce";
    }


    @GetMapping("/come")
    public String comePage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "come";
    }
    @GetMapping("/using")
    public String usingPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "using";
    }
    @GetMapping("/promotion")
    public String promotionPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "promotion";
    }
    @GetMapping("/program")
    public String programPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "program";
    }

    @GetMapping("/hwanbul")
    public String hwanbulPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "hwanbul";
    }
    @GetMapping("/stoppass")
    public String stoppassPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "stoppass";
    }
    @GetMapping("/PTagree")
    public String PTpassPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "PTagree";
    }
    @GetMapping("/gaein")
    public String sgaeinPage(HttpSession session,Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "gaein";
    }
    @GetMapping("/PT")
    public String PTProgramPage(HttpSession session, Model model){
        String username = (String) session.getAttribute("loginUsername");
        String useryd = (String) session.getAttribute("loginUseryd");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);
        model.addAttribute("useryd", false);

        if (useryd != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("useryd", true);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUseryd(useryd);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "PT";
    }


}
