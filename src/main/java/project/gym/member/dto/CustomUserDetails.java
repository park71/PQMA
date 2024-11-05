package project.gym.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.gym.member.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Slf4j
public class CustomUserDetails implements UserDetails {
    private UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    // 정적 메서드 추가
    public static CustomUserDetails fromUserEntity(UserEntity userEntity) {
        return new CustomUserDetails(userEntity);
    }

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // useryd 메서드 추가
    public String getUseryd() {
        return user.getUseryd(); // UserEntity에서 useryd 값을 가져옵니다.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

