package apiserver.apiserver.controller.sign;

import apiserver.apiserver.dto.response.Response;
import apiserver.apiserver.dto.sign.SignInRequest;
import apiserver.apiserver.dto.sign.SignUpRequest;
import apiserver.apiserver.service.sign.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(value = "Sign Controller", tags = "Sign")
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    /*
    요청으로 전달받는 JSON 바디를 객체로 변환하기 위해 @RequestBody를 선언해주고,
    Request 객체의 필드 값을 검증하기 위해 @Valid를 선언해줍니다.
     */
    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req) {
        signService.signUp(req);
        return Response.success();
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest req) {
        return Response.success(signService.signIn(req));
    }

    /*
    요청에 포함되는 Authorization 헤더는 이미 전역적으로 지정되도록 설정해두었기 때문에,
    해당 API에 필요한 요청 헤더는 @ApiIgnore를 선언해줍니다.
     */
    @ApiOperation(value = "토큰 재발급", notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급 받는다.")
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken(@ApiIgnore @RequestHeader(value = "Authorization") String refreshToken) {
        return Response.success(signService.refreshToken(refreshToken));
    }
}
