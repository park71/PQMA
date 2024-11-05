package project.gym.member.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.gym.member.dto.CustomUserDetails;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.UserRepository;

@Slf4j
@Service
@Primary
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;




    @Override
    public UserDetails loadUserByUsername(String useryd) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUseryd(useryd);
        if (user == null) {
            throw new UsernameNotFoundException("User not found for useryd: " + useryd);
        }
        return CustomUserDetails.fromUserEntity(user);
    }
}
