package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeblicNotFoundException extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    public FeblicNotFoundException(){ super(); }

    public FeblicNotFoundException(String message){
        super(message);
    }

    public FeblicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeblicNotFoundException(Throwable cause) {
        super(cause);
    }
}
