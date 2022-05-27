package apiserver.apiserver.controller.category;


import apiserver.apiserver.dto.category.CategoryCreateRequest;
import apiserver.apiserver.dto.sign.SignInResponse;
import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.repository.category.CategoryRepository;
import apiserver.apiserver.entity.repository.member.MemberRepository;
import apiserver.apiserver.init.TestInitDB;
import apiserver.apiserver.service.sign.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static apiserver.apiserver.factory.dto.SignInRequestFactory.createSignInRequest;
import static apiserver.apiserver.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class CategoryControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestInitDB initDB;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SignService signService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
    }

    @Test
    void readAllTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception {
        // given
        CategoryCreateRequest req = createCategoryCreateRequest();
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(initDB.getAdminEmail(), initDB.getPassword()));
        int beforeSize = categoryRepository.findAll().size();

        // when, then
        mockMvc.perform(
                        post("/api/categories")
                                .header("Authorization", adminSignInRes.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(beforeSize + 1);
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        CategoryCreateRequest req = createCategoryCreateRequest();

        // when, then
        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void createAccessDeniedByNormalMemberTest() throws Exception {
        // given
        CategoryCreateRequest req = createCategoryCreateRequest();
        SignInResponse normalMemberSignInRes = signService.signIn(createSignInRequest(initDB.getMember1Email(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        post("/api/categories")
                                .header("Authorization", normalMemberSignInRes.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteTest() throws Exception {
        // given
        /*
        get(0)을 하면 당연히 없기때문에 에러 발생함.
        따라서 0이 아닌경우에 동작하도록 if문 안에 넣어줌
         */
        if(categoryRepository.findAll().size() != 0) {
            Long id = categoryRepository.findAll().get(0).getId();
            SignInResponse adminSignInRes = signService.signIn(createSignInRequest(initDB.getAdminEmail(), initDB.getPassword()));

            // when, then
            mockMvc.perform(delete("/api/categories/{id}", id)
                            .header("Authorization", adminSignInRes.getAccessToken()))
                    .andExpect(status().isOk());
        }
        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception {
        // given
        if(categoryRepository.findAll().size() != 0) {
            Long id = categoryRepository.findAll().get(0).getId();

            // when, then
            mockMvc.perform(delete("/api/categories/{id}", id))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/exception/entry-point"));
        }
    }

    @Test
    void deleteAccessDeniedByNormalMemberTest() throws Exception {
        // given
        if(categoryRepository.findAll().size() !=0) {
            Long id = categoryRepository.findAll().get(0).getId();
            SignInResponse normalMemberSignInRes = signService.signIn(createSignInRequest(initDB.getMember1Email(), initDB.getPassword()));

            // when, then
            mockMvc.perform(delete("/api/categories/{id}", id)
                            .header("Authorization", normalMemberSignInRes.getAccessToken()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/exception/access-denied"));
        }

    }
}
