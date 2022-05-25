package apiserver.apiserver.config.token;

import apiserver.apiserver.handler.JwtHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TokenConfig {  //기존의 TokenService의 역할을 수행

    private final JwtHandler jwtHandler;

    @Bean
    public TokenHelper accessTokenHelper(
            @Value("${jwt.key.access}") String key,
            @Value("${jwt.max-age.access}") long maxAgeSeconds) {
        return new TokenHelper(jwtHandler, key, maxAgeSeconds);
    }

    @Bean
    public TokenHelper refreshTokenHelper(
            @Value("${jwt.key.refresh}") String key,
            @Value("${jwt.max-age.refresh}") long maxAgeSeconds) {
        return new TokenHelper(jwtHandler, key, maxAgeSeconds);
    }
}
