package com.mike.inject_model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends ExceptionContract {

    public NotFoundException(String reason) {
        super(reason);
    }
}
