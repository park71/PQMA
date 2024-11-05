package project.gym.member.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        log.info("Authentication Success: Roles = " + roles);
        if (roles.contains("ROLE_ADMIN")) {
            log.info("Redirecting to /admin");
            response.sendRedirect("/adminPage");

        } else if (roles.contains("ROLE_USER")) {
            log.info("Redirecting to /home");
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/"); // 기본 리다이렉트 URL
        }
    }
}
