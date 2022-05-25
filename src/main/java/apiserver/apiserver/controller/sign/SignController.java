package apiserver.apiserver.controller.sign;

import apiserver.apiserver.dto.response.Response;
import apiserver.apiserver.dto.sign.SignInRequest;
import apiserver.apiserver.dto.sign.SignUpRequest;
import apiserver.apiserver.service.sign.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    /*
    요청으로 전달받는 JSON 바디를 객체로 변환하기 위해 @RequestBody를 선언해주고,
    Request 객체의 필드 값을 검증하기 위해 @Valid를 선언해줍니다.
     */
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req) {
        signService.signUp(req);
        return Response.success();
    }

    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest req) {
        return Response.success(signService.signIn(req));
    }

    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken(@RequestHeader(value = "Authorization") String refreshToken) {
        return Response.success(signService.refreshToken(refreshToken));
    }
}
