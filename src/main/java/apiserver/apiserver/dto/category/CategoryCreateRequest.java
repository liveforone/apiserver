package apiserver.apiserver.dto.category;

import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.repository.category.CategoryRepository;
import apiserver.apiserver.exception.CategoryNotFoundException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@ApiModel(value = "카테고리 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    @ApiModelProperty(value = "카테고리 명", notes = "카테고리 명을 입력해주세요", required = true, example = "my category")
    @NotBlank(message = "{categoryCreateRequest.name.notBlank}")
    @Size(min = 2, max = 30, message = "{categoryCreateRequest.name.size}")
    private String name;

    @ApiModelProperty(value = "부모 카테고리 아이디", notes = "부모 카테고리 아이디를 입력해주세요", example = "7")
    private Long parentId;

    /*
    parentId가 null이라면, Category 생성자의 두번째 인자(부모 Category)도 null이어야 합니다.
    parentId가 null이 아니라면, Category 생성자의 두번째 인자로 부모 Category를 지정해주어야합니다.
    하지만 parentId에 해당하는 id가 없다면, CategoryNotFoundException 예외가 발생해야합니다.
    이러한 로직을 간단히 구현하기 위해, Optional을 이용하였습니다.
     */
    public static Category toEntity(CategoryCreateRequest req, CategoryRepository categoryRepository) {
        return new Category(req.getName(),
                Optional.ofNullable(req.getParentId())
                        .map(id -> categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new))
                        .orElse(null));
    }
}
