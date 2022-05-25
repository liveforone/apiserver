package apiserver.apiserver.factory.dto;

import apiserver.apiserver.dto.sign.SignInResponse;

public class SignInResponseFactory {

    public static SignInResponse createSignInResponse(String accessToken, String refreshToken) {
        return new SignInResponse(accessToken, refreshToken);
    }
}
