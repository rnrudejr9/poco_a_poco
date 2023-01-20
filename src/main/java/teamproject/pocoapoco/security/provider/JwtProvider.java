package teamproject.pocoapoco.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;

import java.util.Date;


@Component
public class JwtProvider {
    private final String SECRET = "asdfknsadfksadnvckasdncksnadkcnklvnzldkknvklxnzsdnnbvklzdfkzfkdnfvk";
    private final long EXPIRATION = 1000 * 60 * 10;
    private final String USERNAME_KEY = "username";

    private final String USERID_KEY = "userid";
    private final String ID_KEY = "id";
    private final String ROLE_KEY = "role";

    public JwtProvider() {
        //secret 과 만기일 재설정
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims();
        claims.put(ID_KEY, user.getId());
        claims.put(USERNAME_KEY, user.getUsername());
        claims.put(ROLE_KEY, user.getRole().name());
        claims.put(USERID_KEY, user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.SignatureException | MalformedJwtException exception) { // 잘못된 jwt signature
        } catch (io.jsonwebtoken.ExpiredJwtException exception) { // jwt 만료
        } catch (io.jsonwebtoken.UnsupportedJwtException exception) { // 지원하지 않는 jwt
        } catch (IllegalArgumentException exception) { // 잘못된 jwt 토큰
        }

        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        Long id = Long.parseLong(claims.get(ID_KEY).toString());
        String username = claims.get(USERNAME_KEY).toString();
        String roleName = claims.get(ROLE_KEY).toString();

        User user = User.builder()
                .id(id)
                .userName(username)
                .role(UserRole.ROLE_USER)
                .build();

        return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
    }

    public String getUserId(String token){

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.get(USERID_KEY, String.class);
    }

    public Long getId(String token){

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.get(ID_KEY, Long.class);
    }
}
