package apiserver.apiserver.factory.entity;

import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.member.Member;
import apiserver.apiserver.entity.post.Image;
import apiserver.apiserver.entity.post.Post;

import java.util.List;

import static apiserver.apiserver.factory.entity.CategoryFactory.createCategory;
import static apiserver.apiserver.factory.entity.MemberFactory.createMember;

public class PostFactory {

    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content", 1000L, member, category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images) {
        return new Post("title", "content", 1000L, member, category, images);
    }

    public static Post createPostWithImages(List<Image> images) {
        return new Post("title", "content", 1000L, createMember(), createCategory(), images);
    }
}
