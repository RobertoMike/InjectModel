package com.mike.inject_model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositoryNotFoundException extends ExceptionContract {

    public RepositoryNotFoundException(String reason) {
        super(reason);
    }
}
