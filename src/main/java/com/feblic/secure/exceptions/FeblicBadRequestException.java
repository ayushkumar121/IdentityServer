package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FeblicBadRequestException extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    @Override
    public HttpStatus getStatus(){ return status; }

    public FeblicBadRequestException(){ super();}

    public FeblicBadRequestException(String message){
        super(message);
    }

    public FeblicBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeblicBadRequestException(Throwable cause) {
        super(cause);
    }
}
