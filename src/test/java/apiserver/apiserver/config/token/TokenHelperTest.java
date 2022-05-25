package apiserver.apiserver.config.token;

import apiserver.apiserver.handler.JwtHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenHelperTest {

    TokenHelper tokenHelper;

    @Mock
    JwtHandler jwtHandler;

    @BeforeEach
    void beforeEach() {
        tokenHelper = new TokenHelper(jwtHandler, "key", 1000L);
    }

    @Test
    void createTokenTest() {
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("token");

        String createdToken = tokenHelper.createToken("subject");

        assertThat(createdToken).isEqualTo("token");
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }

    @Test
    void validateTest() {
        given(jwtHandler.validate(anyString(), anyString())).willReturn(true);

        boolean result = tokenHelper.validate("token");

        assertThat(result).isTrue();
    }

    @Test
    void invalidateTest() {
        given(jwtHandler.validate(anyString(), anyString())).willReturn(false);

        boolean result = tokenHelper.validate("token");

        assertThat(result).isFalse();
    }

    @Test
    void extractSubjectTest() {
        given(jwtHandler.extractSubject(anyString(), anyString())).willReturn("subject");

        String subject = tokenHelper.extractSubject("token");

        assertThat(subject).isEqualTo(subject);
    }

}