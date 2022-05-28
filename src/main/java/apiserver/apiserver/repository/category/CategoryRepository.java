package apiserver.apiserver.repository.category;

import apiserver.apiserver.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
    JPQL로 새로운 쿼리가 작성되어있습니다.
    부모의 아이디로 오름차순 정렬하되 NULL을 우선적으로 하고,
    그 다음으로 자신의 아이디로 오름차순 정렬하여 조회합니다.
     */
    @Query("select c from Category c left join c.parent p order by p.id asc nulls first, c.id asc")
    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

}
