package project.gym.member.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import project.gym.member.dto.CustomUserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {
/*
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    // JWT에서 username을 추출
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("사용자 이름 추출 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    // JWT에서 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // JWT에서 특정 claim 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    // JWT 유효성 검사
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        System.out.println("토큰값생성중?");
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // JWT 유효성 검사 및 username 추출
    public Boolean validateToken(String token, CustomUserDetails userDetails) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}*/
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    // JWT에서 username을 추출
//    public String extractUsername(String token) {
//        try {
//            return extractClaim(token, Claims::getSubject);
//        } catch (Exception e) {
//            log.error("사용자 이름 추출 중 오류 발생: {}", e.getMessage());
//            throw e;
//        }
//    }
    // JWT에서 useryd를 추출
    public String extractUseryd(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("사용자 이름 추출 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    // JWT에서 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // JWT에서 특정 claim 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT 클레임 추출 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    // JWT 유효성 검사
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

//    // JWT 생성
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("roles", userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + expirationMs);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(SignatureAlgorithm.HS256, secret)
//                .compact();
//    }
// JWT 생성
    public String generateToken(UserDetails userDetails) {
        // CustomUserDetails로 형변환
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("useryd", customUserDetails.getUseryd()); // useryd를 claims에 추가
        claims.put("roles", customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getUseryd()) // subject는 useryd 대신 username을 사용할 수 있음
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

//    // JWT 유효성 검사 및 username 추출
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String extractedUsername = extractUsername(token);
//        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
// JWT 유효성 검사 및 useryd 추출
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String extractedUseryd = extractUseryd(token); // useryd로 변경
        return (extractedUseryd.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}