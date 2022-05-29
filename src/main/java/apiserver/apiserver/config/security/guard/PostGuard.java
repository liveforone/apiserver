package apiserver.apiserver.config.security.guard;

import apiserver.apiserver.entity.post.Post;
import apiserver.apiserver.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard {

    private final AuthHelper authHelper;
    private final PostRepository postRepository;

    private boolean hasAuthority(Long id) {
        return hasAdminRole() || isResourceOwner(id); // 1
    }

    private boolean isResourceOwner(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(""); });
        Long memberId = authHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole() {
        return authHelper.extractMemberRoles().contains(RoleType.ROLE_ADMIN);
    }
}
