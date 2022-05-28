package apiserver.apiserver.service.post;

import apiserver.apiserver.dto.post.PostCreateRequest;
import apiserver.apiserver.dto.post.PostCreateResponse;
import apiserver.apiserver.entity.post.Image;
import apiserver.apiserver.entity.post.Post;
import apiserver.apiserver.repository.category.CategoryRepository;
import apiserver.apiserver.repository.member.MemberRepository;
import apiserver.apiserver.repository.post.PostRepository;
import apiserver.apiserver.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    private void uploadImages(List<Image> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        Post post = postRepository.save(
                PostCreateRequest.toEntity(
                        req,
                        memberRepository,
                        categoryRepository
                )
        );
        uploadImages(post.getImages(), req.getImages());
        return new PostCreateResponse(post.getId());
    }
}
