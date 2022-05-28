package apiserver.apiserver.service.file;

import apiserver.apiserver.exception.FileUploadFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class LocalFileService implements FileService {

    @Value("${upload.image.location}")
    private String location; //파일을 업로드할 위치를 주입 받습니다.

    @PostConstruct
    void postConstruct() { //파일을 업로드할 디렉토리를 생성해줍니다.
        File dir = new File(location);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /*
    MultipartFile을 실제 파일로 지정된 위치에 저장해줍니다.
    예외가 발생하면, FileUploadFailureException에 감싸서 던져줍니다.
     */
    @Override
    public void upload(MultipartFile file, String filename) {
        try {
            file.transferTo(new File(location + filename));
        } catch(IOException e) {
            throw new FileUploadFailureException(e);
        }
    }

    @Override
    public void delete(String filename) {

    }
}
