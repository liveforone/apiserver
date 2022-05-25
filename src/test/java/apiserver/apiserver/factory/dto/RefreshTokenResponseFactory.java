package apiserver.apiserver.factory.dto;

import apiserver.apiserver.dto.sign.RefreshTokenResponse;

public class RefreshTokenResponseFactory {

    public static RefreshTokenResponse createRefreshTokenResponse(String accessToken) {
        return new RefreshTokenResponse(accessToken);
    }
}
