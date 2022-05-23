package apiserver.apiserver.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Response {

    private boolean success;
    private int code;
    private Result result;

    public static Response success() {  //요청은 성공했으나, 응답해야할 별다른 데이터가 없을 때 사용
        return new Response(true, 0, null);
    }  //요청 성공시 0 code 갖음

    public static <T> Response success(T data) {
        return new Response(true, 0, new Success<>(data));
    }

    public static Response failure(int code, String msg) {
        return new Response(false, code, new Failure(msg));
    }
}
