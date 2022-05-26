package apiserver.apiserver.factory.dto;

import apiserver.apiserver.dto.category.CategoryCreateRequest;

public class CategoryCreateRequestFactory {

    public static CategoryCreateRequest createCategoryCreateRequest() {
        return new CategoryCreateRequest("category", null);
    }

    public static CategoryCreateRequest createCategoryCreateRequestWithName(String name) {
        return new CategoryCreateRequest(name, null);
    }
}
