package com.feblic.secure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class FeblicUnauthorizedException extends BaseFeblicException{
    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    public FeblicUnauthorizedException() { super(); }

    public FeblicUnauthorizedException(String message) {
        super(message);
    }

    public FeblicUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeblicUnauthorizedException(Throwable cause) {
        super(cause);
    }
}
