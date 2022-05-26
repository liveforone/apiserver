package apiserver.apiserver.service.category;

import apiserver.apiserver.dto.category.CategoryCreateRequest;
import apiserver.apiserver.dto.category.CategoryDto;
import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.repository.category.CategoryRepository;
import apiserver.apiserver.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private boolean notExistsCategory(Long id) {
        return !categoryRepository.existsById(id);
    }

    public List<CategoryDto> readAll() {
        List<Category> categories = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return CategoryDto.toDtoList(categories);
    }

    @Transactional
    public void create(CategoryCreateRequest req) {
        categoryRepository.save(CategoryCreateRequest.toEntity(req, categoryRepository));
    }

    @Transactional
    public void delete(Long id) {
        if(notExistsCategory(id)) throw new CategoryNotFoundException();
        categoryRepository.deleteById(id);
    }
}
