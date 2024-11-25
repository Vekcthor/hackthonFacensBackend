package com.hackthon.dms.controller;

import javax.crypto.BadPaddingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.hackthon.dms.dto.exception.ErrorDTO;
import com.hackthon.dms.exception.GeneralApiError;

@RestControllerAdvice
public class ControllerAdvice {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(GeneralApiError.class)
    public ErrorDTO handlerGeneralApiError(GeneralApiError error){
        return new ErrorDTO(error.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handlerGenericApiError(Exception error){
        return new ErrorDTO(error.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorDTO handlerMultipartApiError(MissingServletRequestParameterException  error){
        return new ErrorDTO("The current '" + error.getParameterName() + "' is missing");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MultipartException .class)
    public ErrorDTO handlerMultiApiError(MultipartException   error){
        return new ErrorDTO(error.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadPaddingException.class)
    public ErrorDTO handleBadPaddingException(BadPaddingException error) {
        return new ErrorDTO("Incorrect passphrase");
    }
}
