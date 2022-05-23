package apiserver.apiserver.learning;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

public class PasswordEncoderTest {

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void encodeWithBcryptTest() {
        String password = "password";

        String encodedPassword = passwordEncoder.encode(password);

        assertThat(encodedPassword).contains("bcrypt");
    }

    @Test
    void matchTest() {
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);

        boolean isMatch = passwordEncoder.matches(password, encodedPassword);

        assertThat(isMatch).isTrue();
    }
}
