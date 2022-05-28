package apiserver.apiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.image.location}")
    private String location;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**") //url에 /image/ 접두 경로가 설정되어있으면,
                .addResourceLocations("file:" + location) //파일 시스템의 location 경로에서 파일에 접근합니다.
                .setCacheControl(CacheControl.maxAge(Duration.ofHours(1L)).cachePublic()); //캐시자원 이용
    }
}
