package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
public class FeblicDependencyFailure extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.FAILED_DEPENDENCY;

    @Override
    public HttpStatus getStatus(){ return status; }

    public FeblicDependencyFailure(){ super(); }

    public FeblicDependencyFailure(String message){
        super(message);
    }

    public FeblicDependencyFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public FeblicDependencyFailure(Throwable cause) {
        super(cause);
    }
}
