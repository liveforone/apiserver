package apiserver.apiserver.controller.sign;

import apiserver.apiserver.dto.sign.SignInRequest;
import apiserver.apiserver.dto.sign.SignInResponse;
import apiserver.apiserver.dto.sign.SignUpRequest;
import apiserver.apiserver.service.sign.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static apiserver.apiserver.factory.dto.RefreshTokenResponseFactory.createRefreshTokenResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SignControllerTest {

    @InjectMocks
    SignController signController;

    @Mock
    SignService signService;

    MockMvc mockMvc;

    //객체를 JSON 문자열로 변환하기 위해 선언해줍니다.
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(signController)
                .build();
    }

    @Test
    void signUpTest() throws Exception {
        SignUpRequest req = new SignUpRequest("email@email.com", "123456a!", "username", "nickname");

        mockMvc.perform(
                post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated());
/*
ObjectMapper.writeValueAsString을 이용하면, 객체를 JSON 문자열로 변환할 수 있습니다.
content에 이를 넣어주면, 요청 바디에 담기게 됩니다.
contentType으로 APPLICATION_JSON 타입을 지정해줍니다.
 */
        verify(signService).signUp(req);
    }

    @Test
    void signInTest() throws Exception {
        // given
        SignInRequest req = new SignInRequest("email@email.com", "123456a!");
        given(signService.signIn(req)).willReturn(new SignInResponse("access", "refresh"));

        // when, then
        mockMvc.perform(
                        post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.accessToken").value("access"))
                /*
                SignService.signIn의 반환 값으로 준비했던 값을, 응답 JSON에 포함되어있는지 검증해줍니다.
                 */
                .andExpect(jsonPath("$.result.data.refreshToken").value("refresh"));

        verify(signService).signIn(req);
    }

    /*
    학습 테스트에서 이미 테스트했던 내용이지만, "/api/sign-up"의 응답 결과로 반환되는
    JSON 문자열 또한 올바르게 제거되는지 다시 한번 검증해보았습니다.
     */
    @Test
    void ignoreNullValueInJsonResponseTest() throws Exception {
        // given
        SignUpRequest req = new SignUpRequest("email@email.com", "123456a!", "username", "nickname");

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void refreshTokenTest() throws Exception {
        // given
        given(signService.refreshToken("refreshToken")).willReturn(createRefreshTokenResponse("accessToken"));

        // when, then
        mockMvc.perform(
                        post("/api/refresh-token")
                                .header("Authorization", "refreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.accessToken").value("accessToken"));
    }
}