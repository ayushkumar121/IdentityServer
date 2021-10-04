package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FeblicInternalException extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    public FeblicInternalException(){ super(); }

    public FeblicInternalException(String message){
        super(message);
    }

    public FeblicInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeblicInternalException(Throwable cause) {
        super(cause);
    }
    
}
