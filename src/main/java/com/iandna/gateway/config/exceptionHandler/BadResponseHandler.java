package com.iandna.gateway.config.exceptionHandler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.iandna.gateway.config.exception.CommonJsonException;
import com.iandna.gateway.config.model.ResponseModel;

@RestControllerAdvice
public class BadResponseHandler {
	@ExceptionHandler(CommonJsonException.class)
    public ResponseModel jsonException(CommonJsonException ex) {
        return ResponseModel.of(ex.getCode(), ex.getMessage());
    }
}
