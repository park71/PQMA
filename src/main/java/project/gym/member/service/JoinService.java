package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.gym.member.dto.JoinDTO;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JoinService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email); // 이메일로 사용자 조회
    }

    public Optional<UserEntity> findByResetToken(String token) {
        return userRepository.findByResetToken(token); // 비밀번호 재설정 토큰으로 사용자 조회
    }

    public void save(UserEntity user) {
        userRepository.save(user); // 사용자 정보 저장
    }
    public void joinProcess(JoinDTO joinDTO) {

        // useryd 공백 및 특수문자 검증 (영문자, 숫자만 허용)
        String userydPattern = "^[a-zA-Z0-9]+$";
        if (!joinDTO.getUseryd().matches(userydPattern)) {
            throw new IllegalArgumentException("아이디는 공백 및 특수문자를 포함할 수 없습니다.");
        }

        // db에 이미 동일한 아이디를 가진 회원이 존재하는지 확인
        boolean isUser = userRepository.existsByUseryd(joinDTO.getUseryd());
        if (isUser) {
            throw new IllegalStateException("이미 존재하는 사용자입니다.");
        }

        String password = joinDTO.getPassword();
        // 최소 하나의 숫자, 최소 하나의 특수문자, 최소 10자 이상을 요구하는 정규식
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{10,}$";
        if (!password.matches(passwordPattern)) {
            throw new IllegalArgumentException("비밀번호는 10자 이상이어야 하며, 최소 하나의 숫자와 특수문자를 포함해야 합니다.");
        }


        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("암호화된 비밀번호: " + encodedPassword);

        // 유저 데이터 설정
        UserEntity data = new UserEntity();
        data.setPhnum(joinDTO.getPhnum());
        data.setPassword(encodedPassword);

        // 기타 정보 설정
        data.setUsername(joinDTO.getUsername());
        data.setUseryd(joinDTO.getUseryd());
        data.setSex(joinDTO.getSex());
        data.setBir(joinDTO.getBir());
        data.setPhnum(joinDTO.getPhnum());
        data.setAddress(joinDTO.getAddress());
        data.setEmail(joinDTO.getEmail());

        // 특정 조건을 만족하는 경우 ROLE_ADMIN으로 설정
        if ("admin@example.com".equals(joinDTO.getEmail())) {
            data.setRole("ROLE_ADMIN");
        } else {
            data.setRole("ROLE_USER");
        }

        userRepository.save(data);
    }

    public void registerAdmin(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            UserEntity admin = new UserEntity();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }
    }

    public UserEntity authenticate(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
    public List<UserEntity> findByUsernames(String username) {
        return userRepository.findAllByUsername(username);
    }

        public JoinDTO logins(JoinDTO joinDTO) {
            // 데이터베이스에서 사용자를 찾음
            UserEntity user = userRepository.findByPassword(joinDTO.getPassword());

            if (user != null) {
                System.out.println("찾은 사용자: " + user.getUsername());
                System.out.println("입력된 비밀번호: " + joinDTO.getPassword());
                System.out.println("저장된 비밀번호: " + user.getPassword());

                // 비밀번호 비교
                if (passwordEncoder.matches(joinDTO.getPassword(), user.getPassword())) {
                    System.out.println("비밀번호 일치 여부: true");

                    // 비밀번호가 일치하면 JoinDTO 객체에 사용자 정보를 설정하여 반환
                    JoinDTO result = new JoinDTO();
                    result.setUsername(user.getUsername());
                    result.setPassword(user.getPassword()); // 이미 암호화된 비밀번호를 설정
                    result.setRole(user.getRole());
                    return result;
                } else {
                    System.out.println("비밀번호 일치 여부: false");
                }
            } else {
                System.out.println("사용자를 찾을 수 없습니다.");
            }

            // 비밀번호가 일치하지 않거나 사용자를 찾지 못한 경우 null 반환
            return null;
        }


    public List<JoinDTO> findAll() {
        List<UserEntity> memberEntityList = userRepository.findAll();
        List<JoinDTO> memberDTOList = new ArrayList<>();
        for (UserEntity userEntity: memberEntityList) {
            memberDTOList.add(JoinDTO.toMemberDTO(userEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }
 //   public JoinDTO findById(Integer id) {
 //       Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
 //       if (optionalUserEntity.isPresent()) {
//            MemberEntity memberEntity = optionalMemberEntity.get();
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            return memberDTO;
   //         return JoinDTO.toMemberDTO(optionalUserEntity.get());
  //      } else {
   //         return null;
    //    }

 //   }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("glaehekd312@naver.com");  // 발신자 이메일 주소 설정
        message.setTo(toEmail);
        message.setSubject("비밀번호 재설정 요청");
        message.setText("비밀번호를 재설정하려면 아래 링크를 클릭하세요:\n" + resetLink);

        mailSender.send(message);
    }

    public Optional<UserEntity> findByUseryd(String useryd){
        return userRepository.findOptionalByUseryd(useryd);
    }



    public JoinDTO updateForm(String useryd) {
        UserEntity userEntity = userRepository.findByUseryd(useryd);
        if (userEntity != null) {
            return JoinDTO.toMemberDTO(userEntity);
        } else {
            return null;
        }
    }

    // ID 중복 여부 확인 메소드
    public boolean isUserydTaken(String useryd) {
        return userRepository.existsByUseryd(useryd);
    }

}
