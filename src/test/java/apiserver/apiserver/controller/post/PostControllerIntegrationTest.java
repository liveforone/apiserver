package apiserver.apiserver.controller.post;

import apiserver.apiserver.dto.post.PostCreateRequest;
import apiserver.apiserver.dto.sign.SignInResponse;
import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.member.Member;
import apiserver.apiserver.entity.post.Post;
import apiserver.apiserver.exception.MemberNotFoundException;
import apiserver.apiserver.init.TestInitDB;
import apiserver.apiserver.repository.category.CategoryRepository;
import apiserver.apiserver.repository.member.MemberRepository;
import apiserver.apiserver.repository.post.PostRepository;
import apiserver.apiserver.service.sign.SignService;
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

import static apiserver.apiserver.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static apiserver.apiserver.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static apiserver.apiserver.factory.dto.SignInRequestFactory.createSignInRequest;
import static apiserver.apiserver.factory.entity.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PostControllerIntegrationTest {

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
    PostRepository postRepository;
    @Autowired
    SignService signService;

    Member member;
    Category category;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
        member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        category = categoryRepository.findAll().get(0);
    }

    @Test
    void createTest() throws Exception {
        // given
        SignInResponse signInRes = signService.signIn(createSignInRequest(member.getEmail(), initDB.getPassword()));
        PostCreateRequest req = createPostCreateRequest("title", "content", 1000L, member.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getMember().getId()).isEqualTo(member.getId()); // 1
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        PostCreateRequest req = createPostCreateRequest("title", "content", 1000L, member.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void readTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member, category));

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk());
    }
}
