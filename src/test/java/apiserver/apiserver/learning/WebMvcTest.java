package apiserver.apiserver.learning;

import apiserver.apiserver.controller.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

    @InjectMocks
    TestController testController;

    MockMvc mockMvc;

    @Controller // 2
    public static class TestController {
        @GetMapping("/test/ignore-null-value")
        public Response ignoreNullValueTest() {
            return Response.success();
        }
    }

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(testController)
                .build();
    }

    /*
    MockMvc.perform으로 요청을 보내고, 그 결과를 검증할 수 있습니다. "/test/ignore-null-value"로 get 요청을 보낸 뒤, 응답 상태 코드 200과 응답 JSON에 result 필드가 없음을 확인하였습니다.
     */
    @Test
    void ignoreNullValueInJsonResponseTest() throws Exception {
        mockMvc.perform(get("/test/ignore-null-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").doesNotExist());
    }
}
