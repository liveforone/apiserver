package apiserver.apiserver.controller.member;

import apiserver.apiserver.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    MemberController memberController;

    @Mock
    MemberService memberService;

    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    void readTest() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        get("/api/members/{id}", id))
                .andExpect(status().isOk());
        verify(memberService).read(id);
    }

    @Test
    void deleteTest() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/members/{id}", id))
                .andExpect(status().isOk());
        verify(memberService).delete(id);
    }
}