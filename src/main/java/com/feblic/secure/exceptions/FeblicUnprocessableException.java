package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class FeblicUnprocessableException extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    @Override
    public HttpStatus getStatus() {return status;}

    public FeblicUnprocessableException(){ super(); }

    public FeblicUnprocessableException(String message){super(message);}

    public FeblicUnprocessableException(String message,Throwable cause){
        super(message,cause);
    }

    public FeblicUnprocessableException(Throwable cause){super(cause);}
}
