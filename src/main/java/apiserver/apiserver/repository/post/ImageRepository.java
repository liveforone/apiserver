package apiserver.apiserver.repository.post;

import apiserver.apiserver.entity.post.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
