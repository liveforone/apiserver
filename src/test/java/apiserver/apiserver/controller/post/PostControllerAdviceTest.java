package apiserver.apiserver.controller.post;

import apiserver.apiserver.advice.ExceptionAdvice;
import apiserver.apiserver.dto.post.PostCreateRequest;
import apiserver.apiserver.exception.CategoryNotFoundException;
import apiserver.apiserver.exception.MemberNotFoundException;
import apiserver.apiserver.exception.PostNotFoundException;
import apiserver.apiserver.exception.UnsupportedImageFormatException;
import apiserver.apiserver.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static apiserver.apiserver.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerAdviceTest {

    /*
    @InjectMocks PostController postController;
    @Mock PostService postService;
    @Mock ResponseHandler responseHandler;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        mockMvc = MockMvcBuilders.standaloneSetup(postController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void createExceptionByMemberNotFoundException() throws Exception{
        // given
        given(postService.create(any())).willThrow(MemberNotFoundException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound());
    }

    @Test
    void createExceptionByCategoryNotFoundException() throws Exception{
        // given
        given(postService.create(any())).willThrow(CategoryNotFoundException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound());
    }

    @Test
    void createExceptionByUnsupportedImageFormatException() throws Exception{
        // given
        given(postService.create(any())).willThrow(UnsupportedImageFormatException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound());
    }

    @Test
    void readExceptionByPostNotFoundTest() throws Exception {
        // given
        given(postService.read(anyLong())).willThrow(PostNotFoundException.class);

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExceptionByPostNotFoundTest() throws Exception {
        // given
        doThrow(PostNotFoundException.class).when(postService).delete(anyLong());

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExceptionByPostNotFoundTest() throws Exception{
        // given
        given(postService.update(anyLong(), any())).willThrow(PostNotFoundException.class);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", 1L)
                                .param("title", "title")
                                .param("content", "content")
                                .param("price", "1234")
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    private ResultActions performCreate() throws Exception {
        PostCreateRequest req = createPostCreateRequest();
        return mockMvc.perform(
                multipart("/api/posts")
                        .param("title", req.getTitle())
                        .param("content", req.getContent())
                        .param("price", String.valueOf(req.getPrice()))
                        .param("categoryId", String.valueOf(req.getCategoryId()))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA));
    }
    */
}
