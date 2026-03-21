package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.exception;

public class KakaoApiException extends RuntimeException {

    private final int statusCode;

    public KakaoApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public KakaoApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
