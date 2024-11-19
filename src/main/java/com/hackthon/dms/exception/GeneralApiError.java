package com.hackthon.dms.exception;

public class GeneralApiError extends RuntimeException {
    
    public GeneralApiError(String error){
        super(error);
    }
}
