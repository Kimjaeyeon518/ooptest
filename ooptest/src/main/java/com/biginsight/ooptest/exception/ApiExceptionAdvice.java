package com.biginsight.ooptest.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.spring.usinsa.controller.api.v1"})
@Slf4j
public class ApiExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResponse defaultException(Exception e) {
        CommonResponse commonResponse = new CommonResponse();
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            ApiErrorCode apiErrorCode = apiException.getErrorCode();
            commonResponse.setSuccess(false);
            commonResponse.setCode(apiErrorCode.getStatusCode());
            commonResponse.setMsg(apiErrorCode.getMessage());
        }
        else if(e instanceof Exception){
            log.error(e.toString());
            commonResponse.setSuccess(false);
            commonResponse.setCode(-1);
            commonResponse.setMsg(e.getMessage());
        }

        return commonResponse;
    }

}