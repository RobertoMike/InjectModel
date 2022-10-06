package com.mike.inject_model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends NotFoundContract {


    public NotFoundException(String reason) {
        super(reason);
    }
}
