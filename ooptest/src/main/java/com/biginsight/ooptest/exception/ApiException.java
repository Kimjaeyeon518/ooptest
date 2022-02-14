package com.biginsight.ooptest.exception;

import lombok.Getter;

public class ApiException extends RuntimeException{
    @Getter
    private ApiErrorCode errorCode;

    public ApiException(String message, ApiErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, Throwable t) {
        super(errorCode.getMessage(), t);
    }


    public ApiException(String msg, Throwable t) {
        super(msg, t);
    }

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException() {
        super();
    }
}