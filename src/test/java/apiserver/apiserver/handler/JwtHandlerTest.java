package apiserver.apiserver.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtHandlerTest {

    JwtHandler jwtHandler = new JwtHandler();

    @Test
    void createTokenTest() {
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes(StandardCharsets.UTF_8));
        String token = createToken(encodedKey, "subject", 60L);

        assertThat(token).contains("Bearer");
    }

    @Test
    void extractSubjectTest() {
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes(StandardCharsets.UTF_8));
        String subject = "subject";
        String token = createToken(encodedKey, subject, 60L);

        String extractSubject = jwtHandler.extractSubject(encodedKey, token);
        assertThat(extractSubject).isEqualTo(subject);

    }

    @Test
    void validateTest() {
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 60L);

        boolean isValid = jwtHandler.validate(encodedKey, token);

        assertThat(isValid).isTrue();
    }

    @Test
    void invalidateByInvalidKeyTest() {  //토큰 생성에 사용했던 key 외에, 다른 key를 사용하여 토큰을 검증할 경우, 토큰은 유효하지 않습니다.
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 60L);

        // when
        boolean isValid = jwtHandler.validate("invalid", token);

        assertThat(isValid).isFalse();
    }

    @Test
    void invalidateByExpiredTokenTest() {
        /*토큰의 유효 기간을 0초로 설정하여 생성하면, 토큰 발급과 동시에 토큰은 만료됩니다.
        따라서 토큰의 유효성 검사는 실패합니다.
         */
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 0L);

        // when
        boolean isValid = jwtHandler.validate(encodedKey, token);

        // then
        assertThat(isValid).isFalse();
    }

    private String createToken(String encodedKey, String subject, long maxAgeSeconds) {
        return jwtHandler.createToken(
                encodedKey,
                subject,
                maxAgeSeconds
        );
    }
}