package apiserver.apiserver.exception;

public class MemberNicknameAlreadyExistsException extends RuntimeException {
    public MemberNicknameAlreadyExistsException(String message) {
        super(message);
    }
}
