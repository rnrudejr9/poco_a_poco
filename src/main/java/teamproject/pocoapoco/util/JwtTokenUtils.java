package teamproject.pocoapoco.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtils {

    public static String createToken(String userName, String key, long expireTime){

        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public static Claims parseClaims(String token, String secretKey){

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public static boolean isExpired(String token, String secretKey){

        Claims claims = parseClaims(token, secretKey);
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    public static String getUserName(String token, String secretKey){

        Claims claims = parseClaims(token, secretKey);
        return claims.get("userName", String.class);
    }

}
