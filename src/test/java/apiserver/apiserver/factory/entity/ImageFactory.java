package apiserver.apiserver.factory.entity;

import apiserver.apiserver.entity.post.Image;

public class ImageFactory {

    public static Image createImage() {
        return new Image("origin_filename.jpg");
    }

    public static Image createImageWithOriginName(String originName) {
        return new Image(originName);
    }
}
