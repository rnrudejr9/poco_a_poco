package teamproject.pocoapoco.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import teamproject.pocoapoco.controller.main.ui.ViewController;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
@Slf4j
public class JwtProvider {
    private final String SECRET = "asdfknsadfksadnvckasdncksnadkcnklvnzldkknvklxnzsdnnbvklzdfkzfkdnfvk";
    private final Long ACCESS_EXPIRATION = 1000 * 60 * 30L; // 30분
    public final Long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 14L; // 2주
    private final String USERNAME_KEY = "username";
    private final String USERID_KEY = "userid";
    private final String ID_KEY = "id";
    private final String ROLE_KEY = "role";

    public JwtProvider() {
        //secret 과 만기일 재설정
    }

    /***
     * 토큰 생성
     * @param user
     * @return token
     */
    public String generateAccessToken(User user) {
        Claims claims = Jwts.claims();
        claims.put(ID_KEY, user.getId());
        claims.put(USERNAME_KEY, user.getUsername());
        claims.put(ROLE_KEY, user.getRole().name());
        claims.put(USERID_KEY, user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.SignatureException | MalformedJwtException exception) { // 잘못된 jwt signature
//            log.info("validateToken : 잘못된 시그니처");
            throw new AppException(ErrorCode.INVALID_TOKEN, "validateToken : 잘못된 시그니처");
        } catch (io.jsonwebtoken.ExpiredJwtException exception) { // jwt 만료
//            log.info("validateToken : jwt 만료");
            throw new AppException(ErrorCode.EXPIRED_TOKEN, "validateToken : jwt 만료");
        } catch (io.jsonwebtoken.UnsupportedJwtException exception) { // 지원하지 않는 jwt
//            log.info("validateToken : 지원하지 않는 jwt");
            throw new AppException(ErrorCode.INVALID_TOKEN, "validateToken : 지원하지 않는 jwt");
        } catch (IllegalArgumentException exception) { // 잘못된 jwt 토큰
            log.info("잘못된 jwt 토큰");
            throw new AppException(ErrorCode.INVALID_TOKEN, "validateToken : 잘못된 jwt 토큰");
        }
    }


    public Authentication getAuthentication(String accessToken) {
        // token 복호화
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(accessToken).getBody();
        Long id = Long.parseLong(claims.get(ID_KEY).toString());
        String username = claims.get(USERNAME_KEY).toString();
        String roleName = claims.get(ROLE_KEY).toString();

        User user = User.builder()
                .id(id)
                .userName(username)
                .role(UserRole.valueOf(roleName))
                .build();

        return new UsernamePasswordAuthenticationToken(user, accessToken, user.getAuthorities());
    }

    public String getUserId(String token){

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.get(USERID_KEY, String.class);
    }

    public Long getId(String token){

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.get(ID_KEY, Long.class);
    }
    /***
     * 토큰의 유효시간 확인
     * @param token
     * @return accessToken or refreshToken 의 현재 남은 유효시간
     */
    public Long getCurrentExpiration(String token) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
