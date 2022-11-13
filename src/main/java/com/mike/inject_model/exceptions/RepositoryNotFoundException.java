package com.mike.inject_model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositoryNotFoundException extends NotFoundContract {

    public RepositoryNotFoundException(String reason) {
        super(reason);
    }
}
