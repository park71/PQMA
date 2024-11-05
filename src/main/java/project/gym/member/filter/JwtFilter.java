package project.gym.member.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.gym.member.service.CustomUserDetailService;

import java.io.IOException;


@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private  JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailService  userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 특정 URL 패턴 제외
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("/favicon.ico") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".css")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestURI.startsWith("/oauth2/") || requestURI.startsWith("/login/oauth2/") || requestURI.startsWith("/kakao/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JwtFilter 요청이 도달하였습니다.");
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request Method: {}", request.getMethod());


        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        log.info("쿠키에서 추출한 JWT 토큰값: {}", jwt);

//        if (jwt != null) {
//            String username = jwtUtil.extractUsername(jwt);
//            log.info("JWT 토큰에서 추출한 사용자 이름: {}", username);
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                if (jwtUtil.validateToken(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//        }
        if (jwt != null) {
            String useryd = jwtUtil.extractUseryd(jwt); // 변경: username 대신 useryd 추출
            log.info("JWT 토큰에서 추출한 사용자 ID: {}", useryd);
            if (useryd != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(useryd); // useryd로 변경
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}
