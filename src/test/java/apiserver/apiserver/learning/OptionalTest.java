package apiserver.apiserver.learning;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionalTest {

    @Test
    void doseNotInvokeOptionalInnerFunctionByOuterNullValueTest() {
        Long result = Optional.ofNullable(null)
                .map(id -> Optional.ofNullable((Long) null).orElseThrow(RuntimeException::new))
                .orElse(5L);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void catchWhenExceptionIsThrownInOptionalInnerFunctionTest() {
        assertThatThrownBy(
                () -> Optional.ofNullable(5L)
                        .map(id -> Optional.ofNullable((Long) null).orElseThrow(RuntimeException::new))
                        .orElse(1L))
                .isInstanceOf(RuntimeException.class);
    }
}
