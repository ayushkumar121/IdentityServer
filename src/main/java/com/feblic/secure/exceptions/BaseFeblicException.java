package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseFeblicException extends RuntimeException {

    public BaseFeblicException(){super();}

    public BaseFeblicException(String message){super(message);}

    public BaseFeblicException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseFeblicException(Throwable cause) {super(cause);}

    abstract public HttpStatus getStatus();
}
