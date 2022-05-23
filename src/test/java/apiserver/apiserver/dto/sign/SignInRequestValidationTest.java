package apiserver.apiserver.dto.sign;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class SignInRequestValidationTest {

    //검증 작업을 수행하기 위해 Validator를 빌드해줍니다.
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private SignInRequest createRequest() { // 6
        return new SignInRequest("email@email.com", "123456a!");
    }

    private SignInRequest createRequestWithEmail(String email) { // 7
        return new SignInRequest(email, "123456a!");
    }

    private SignInRequest createRequestWithPassword(String password) { // 8
        return new SignInRequest("email@email.com", password);
    }

    @Test
    void validateTest() {
        SignInRequest req = createRequest();

        //검증을 수행합니다. 제약 조건을 위반한 내용들을 응답 결과로 받게 됩니다.
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //제약 조건을 모두 지키고 올바르게 검증되었다면, 응답 결과는 비어있습니다
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNotFormattedEmailTest() {
        // given
        String invalidValue = "email";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        //하지만 제약 조건을 위반했다면, 응답 결과는 비어있지 않습니다.
        assertThat(validate).isNotEmpty();
        //응답 결과에서 제약 조건을 위반한 객체를 꺼내서, given에서 설정해두었던 위반된 값을 가지고 있는지 확인합니다.
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyEmailTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankEmailTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyPasswordTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankPasswordTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithPassword(" ");

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

}