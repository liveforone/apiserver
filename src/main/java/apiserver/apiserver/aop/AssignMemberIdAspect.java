package apiserver.apiserver.aop;

import apiserver.apiserver.config.security.guard.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignMemberIdAspect {

    private final AuthHelper authHelper;

    private Optional<Method> getMethod(Class<?> clazz, String methodName) {
        try {
            return Optional.of(clazz.getMethod(methodName, Long.class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private void invokeMethod(Object obj, Method method, Object... args) {
        try {
            method.invoke(obj, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }

    @Before("@annotation(apiserver.apiserver.aop.AssignMemberId)")
    public void assignMemberId(JoinPoint joinPoint) {
        /*
        JoinPoint.getArgs()를 이용하여 전달되는 인자들을 확인하고,
        setMemberId 메소드가 정의된 타입이 있다면, memberId를 주입해줍니다.
         */
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg -> getMethod(arg.getClass(), "setMemberId")
                        .ifPresent(setMemberId -> invokeMethod(arg, setMemberId, authHelper.extractMemberId())));
    }
}
