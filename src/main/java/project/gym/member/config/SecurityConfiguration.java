package project.gym.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.gym.member.filter.JwtFilter;
import project.gym.member.filter.JwtUtil;
import project.gym.member.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration  {

    private final JwtFilter jwtFilter;

    public SecurityConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /*@Autowired
    public SecurityConfiguration(CustomUserDetailService customUserDetailService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.customUserDetailService = customUserDetailService;
        this.jwtUtil = jwtUtil;
        this.userRepository=userRepository;
    }
*/
    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/stat/reset","/favicon.ico", "/home","/inbodys/**", "/", "/sync-sheets","/login", "/loginProc", "/join", "/joinProc","/qr/members","/qrcode",
                                "/info","/event","/css/**", "/img/**", "/js/**", "/images/**","/lock-check","/resetPassword","/qrcode/**","/entry/from-qr",
                                "/login?error", "/list", "/consultationForm", "/consultation","/PT","/agreementuser","userdashboard","/contracts/**","/sheets/read",
                                "/introduce","/resetPassword","/forgotPassword","/resetPassword/**", "/come", "/using", "/promotion", "/program", "/gong", "/hwanbul", "/stoppass", "/PTagree", "/gaein","/member/apply","/apply",
                                "/becon", "/beconsult", "/dashboard", "/board/**", "/board/list", "/boardlist","/PTagreementuser","/member/PT_apply","/member/trans","/member/stop","/member/cashback","/api/members/search",
                                "/api/transfers/trans","/api/**","/api/lockers/collect","/check-duplicate-id","/member/gaepop.html","/member/usepop.html","/member/yangpop.html","/member/hwanpop.html","/member/pmpop.html",
                                "/board/modify/**","/kakao/admin/login", "/login/oauth2/code/kakao", "/oauth2/**","/files/**","/pauseFormuser","/cashbackForm","/member/refund","/member/ptcashback","/api/members/loggedin",
                                "/kakao/admin/login-success","/board/view", "/message", "/boardview", "/board/file/**", "/board/update", "/login/oauth2/code/kakao", "/oauth2/authorization/kakao", "/kauth/**").permitAll()
                        .requestMatchers("/adminPage", "/board/writepro", "/boardwrite", "/consultationList", "/transfer", "/search","/admin/PT_apply","/admin/**","/admin/PTMembershipApplication",
                                "/agreement","/closer","/cost","/getCalendarData","/getRevenueByDate","/coster","/finalize","/lockout","/updateStatus", "/longabsent","/inbody/**", "/memberList","/boardmodify","/PTinfo","/PTagreement","/admin/restApplication","/admin/transApplication","/admin/cashbackApplication","/admin/memberships-apply/**",
                                "/memberList/**", "/lockers", "/entry","/entries","/entrylist","/decrement","/membershipApplication","/member/memo","/memo","/member/**","/PTinfofix",
                                "/entrySuccess","/entrySuccessful", "/membershipList","/longtime","/lastday","/entryFailure", "/pauseForm", "/pauseList", "/member/**","/PTList").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .defaultSuccessUrl("/adminPage", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }




    public AuthenticationManager authenticationManagerHttp(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder(); // PasswordEncoder로 BCryptPasswordEncoder 사용
   }


}
