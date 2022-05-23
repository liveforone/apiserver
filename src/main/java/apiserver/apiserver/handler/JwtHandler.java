package apiserver.apiserver.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtHandler {

    private String type = "Bearer";

    private Jws<Claims> parse(String key, String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(untype(token));  //토큰 문자열에는 토큰의 타입도 포함되어있으므로, 이를 untype 메소드를 이용하여 제거해줍니다.
    }

    private String untype(String token) {
        return token.substring(type.length());
    }

    public String createToken(String encodedKey, String subject, long maxAgeSeconds) {
        //토큰에 저장될 데이터 -> subject  ||  만료 기간 maxAgeSeconds
        Date now = new Date();
        return type + Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L))
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();
    }

    public String extractSubject(String encodedKey, String token) {
        return parse(encodedKey, token).getBody().getSubject();
    }

    public boolean validate(String encodedKey, String token) {
        try {
            parse(encodedKey, token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
