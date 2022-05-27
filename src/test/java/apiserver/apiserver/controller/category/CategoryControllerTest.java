package apiserver.apiserver.controller.category;

import apiserver.apiserver.dto.category.CategoryCreateRequest;
import apiserver.apiserver.dto.sign.SignInResponse;
import apiserver.apiserver.service.category.CategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.verify;
import static apiserver.apiserver.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryService categoryService;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void readAllTest() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
        verify(categoryService).readAll();
    }

    @Test
    void createTest() throws Exception {
        CategoryCreateRequest req = createCategoryCreateRequest();
        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(categoryService).create(req);
    }


    @Test
    void deleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/categories/{id}", id))
                .andExpect(status().isOk());
        verify(categoryService).delete(id);
    }
}