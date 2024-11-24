package project.gym.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.gym.member.entity.UserEntity;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDTO {
    private int id;

    private String useryd;
    private String username;
    private String password;
    private String sex;
    private LocalDate bir;
    private String phnum;
    private String address;
    private String email;

    private String role;
    private String accessToken;

    public static JoinDTO toMemberDTO(UserEntity userEntity) {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setId(userEntity.getId());
        joinDTO.setUsername(userEntity.getUsername());
        joinDTO.setUseryd(userEntity.getUseryd());
        joinDTO.setPassword(userEntity.getPassword());
        joinDTO.setBir(userEntity.getBir());
        joinDTO.setSex(userEntity.getSex());
        joinDTO.setPhnum(userEntity.getPhnum());
        joinDTO.setAddress(userEntity.getAddress());
        joinDTO.setEmail(userEntity.getEmail());
        joinDTO.setRole(userEntity.getRole());
        return joinDTO;
    }
}

